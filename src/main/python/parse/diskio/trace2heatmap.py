#!/usr/bin/env python

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from __future__ import unicode_literals

from trace2list import trace2list
from trace2list import IOTraceItem
from collections import defaultdict
from utils import Logger
from utils import die

import tempfile
import argparse
import subprocess
import os
import re
import math

def dump_counts(counts):
    with open('/tmp/t2h_debug.txt', 'w') as f:
        for key, value in counts.iteritems():
            f.write(str(key) + ': ' + str(value) + '\n') 

def run_cmd(command, lines=False):
    output = None
    try:
        output = subprocess.check_output(
                command,
                )
    except subprocess.CalledProcessError as e:
        die(' '.join(e.cmd) + ' returned ' + str(e.returncode) + '\n')
    return output

def file_info(item, files):
    name = item.file_name
    for file in files:
        if file.name == name:
            return file
    return None

def root_dir():
    root = run_cmd(['hg', 'root'])
    if not root:
        root = run_cmd(['git', 'rev-parse', '--show-toplevel'])
        if not root:
            die('Unable to find root-dir. Aborting\n')
    return root.strip()

def heatmap_tool_path():
    PATH = 'fbandroid/third-party/viz-tools/heatmap/trace2heatmap.pl'
    root = root_dir()
    return os.path.join(root, PATH)

def parse_args():
    parser = argparse.ArgumentParser(description='Converts I/O trace to SVG heatmap')
    parser.add_argument(
            '--input',
            required=True,
            dest='path',
            help='path to input file')
    parser.add_argument(
            '--output',
            dest='output',
            help='path to heatmap svg')
    parser.add_argument(
            '--type-filter',
            dest='type_filter',
            choices=['R', 'W'],
            default='R',
            help='show only events of this type. possible values are R|W')
    parser.add_argument(
            '--verbose',
            action='store_true',
            help='Enable verbose mode')
    parser.add_argument(
            '--debug',
            action='store_true',
            default=False,
            help='Dont delete temp files')
    parser.add_argument(
            '--access-pattern',
            action='store_true',
            default=False,
            help='Visualize access patterns')

    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument(
            '--file-filter',
            type=str,
            dest='file_filter',
            help='Name of the file whose I/O is to be mapped')
    group.add_argument(
            '--list-files',
            action='store_true',
            help='List of all files in given input')

    return parser.parse_args()

# Invoke heatmap tool with given heatmap data and other parameters
def write_heatmap(input, output, title, subtitle):
    path = heatmap_tool_path()

    if not os.path.exists(input):
        die(input + ' not found')

    if not os.path.exists(path):
        die(path + ' not found')

    cmd = [path, '--boxsize=16', '--title=' + title, '--subtitle=' + subtitle, input]
    print('Running cmd: ' + ' '.join(cmd))
    svg = run_cmd(cmd)
    if not svg:
        die('Unable to get heatmap svg. Aborting...\n')

    with open(output, 'w') as o:
        for line in svg:
            o.write(line)

    print('Wrote SVG heatmap to ' + output)

# Given a list of page read/write events along with the index of pages
# read from/written to, generate a heatmap of page read/write counts
# This method just formats the page data to correct format expected by the
# heatmap tool in /third-party/viz-tools/heatmap
def trace2heatmap(access_pattern, items, files, output, title, subtitle, debug):
    PAGE_SIZE = 4096  # 4KB
    MAX_ROWS = 50

    if len(items) > 1:
        die('More than 1 file matched the filter. Please refine \
            the filter to match only 1 file. Aborting...\n')

    if len(items) == 0:
        die('No files matched the specified filter. Aborting...\n')

    # Compute number of rows/columns based on file size
    item = items[0]
    size = file_info(item, files).size
    nr_pages = int(size / PAGE_SIZE)
    nr_rows = MAX_ROWS
    nr_cols = int(nr_pages / nr_rows)

    heatmap = []
    debug_counts = defaultdict(int)
    pages = item.pages

    index = 0
    # The heatmap tool expects the input to have the following format:
    # col-index row-index
    # And it calculates the heatmap colors based on the number of occurrences
    # of each (col-index,row-index) pair
    # So we just convert a page number into the corresponding row/col index
    for page in pages:
        index += 1
        debug_counts[page] = debug_counts[page] + 1
        line = str(int(page % nr_cols)) + ' ' + str(int(page / nr_cols))
        if access_pattern:
            for i in range(0, index):
                heatmap.append(line)
        else:
            heatmap.append(line)

    if debug:
        temp = open('/tmp/t2h_data.txt', 'w')
        dump_counts(debug_counts)
    else:
        temp = tempfile.NamedTemporaryFile()

    # Write out heatmap data to temp file
    try:
        for item in heatmap:
            temp.write(item + '\n')
        temp.flush()
        write_heatmap(temp.name, output, title, subtitle)
    finally:
        temp.close()

def main():
    args = parse_args()

    if args.verbose:
        Logger.enableInfo()
    else:
        Logger.enableError()

    # To get the required data, we need to group filter + group by given file
    filter = 'file:' + args.file_filter if args.file_filter else ''
    group_by = 'file'
    
    list, files = trace2list(args.path, args.type_filter, filter, group_by)

    # If we're asked to list all files, we don't need to generate a heatmap
    if args.list_files:
        # print items
        for file in files:
            print('{} ({} bytes, {} reads, {} writes)'.format(
                    file.name, file.size, file.nr_reads, file.nr_writes))
    else:
        # Generate heatmap
        output = args.output if args.output else './heatmap.svg'
        type_str = 'Reads' if args.type_filter == 'R' else 'Writes'
        title = 'Disk ' + type_str
        subtitle = filter
        trace2heatmap(args.access_pattern, list, files, output, title, subtitle, args.debug)

if __name__ == "__main__":
    main()
