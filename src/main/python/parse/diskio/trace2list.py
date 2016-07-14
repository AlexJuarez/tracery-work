from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from __future__ import unicode_literals

from trace_parser import TraceParser
from collections import defaultdict
import argparse
import re

# IO trace format:
# Timestamp Op Count Thread CPU Filename Pg-Count Duration Pages Sectors
class IOTraceItem:
    def __init__(self, ts, type, cnt, thread, cpu, file, pg_cnt, dur, pages, sectors):
        self.timestamp = ts
        self.type = type
        self.count = cnt
        self.thread_name = thread
        self.cpu = cpu
        self.file_name = file
        self.page_count = pg_cnt
        self.duration = dur
        self.pages = pages
        self.sectors = sectors

    @staticmethod
    def null_obj():
        return IOTraceItem(0, '', 0, '', 0, '', 0, 0, [], [])
    
    @staticmethod
    def all_col_names():
        return IOTraceItem.null_obj()._to_str([], True)

    @staticmethod
    def col_names(hidden_cols):
        return IOTraceItem.null_obj()._to_str(hidden_cols, True)

    def to_str(self, hidden_cols):
        return self._to_str(hidden_cols, False)

    def _to_str(self, hidden_cols, print_hdr):
        SEP = ' | '
        output = SEP
        if 'timestamp' not in hidden_cols:
            ts = 'timestamp' if print_hdr else str(self.timestamp)
            output += (ts + SEP)
        if 'type' not in hidden_cols:
            type = 'type' if print_hdr else self.type
            output += (type + SEP)
        if 'count' not in hidden_cols:
            count = 'count' if print_hdr else str(self.count)
            output += (count + SEP)
        if 'thread' not in hidden_cols:
            thread = 'thread' if print_hdr else self.thread_name
            output += (thread + SEP)
        if 'cpu' not in hidden_cols:
            cpu = 'cpu' if print_hdr else str(self.cpu)
            output += (cpu + SEP)
        if 'file' not in hidden_cols:
            file = 'file' if print_hdr else self.file_name
            output += (file + SEP)
        if 'page_count' not in hidden_cols:
            pc = 'page-count' if print_hdr else str(self.page_count)
            output += (pc + SEP)
        if 'duration' not in hidden_cols:
            duration = 'duration' if print_hdr else str(self.duration)
            output += (duration + SEP)
        if 'pages' not in hidden_cols:
            pages = 'pages' if print_hdr \
                    else ','.join(str(page) for page in self.pages)
            output += (pages + SEP)
        if 'sectors' not in hidden_cols:
            sectors = 'sectors' if print_hdr \
                    else ','.join(str(sector) for sector in self.sectors)
            output += (sectors + SEP)

        return output

def matches(event, filter):
    if not filter:
        return True

    key, value = filter.split(':')

    if not key or not value:
        return False

    if key == 'file':
        if re.match(value, event.file_name):
            return True
        return False

    if key == 'thread':
        if re.match(value, event.thread_name):
            return True
        return False

def page_count(event):
    if event.type == 'R':
        return event.page_count
    if event.type == 'W':
        return 1
    return 0

def pages(event):
    if event.type == 'R' or event.type == 'W':
        return event.pages
    return []

def sectors(event):
    # TBD
    return []

def group_key(group_by, event):
    if group_by == 'file':
        return event.file_name
    if group_by == 'thread':
        return event.thread_name
    if group_by == 'type':
        return event.type

def group_item(group_by, event):
    file_name = thread_name = type = '-'
    if group_by == 'file':
        file_name = event.file_name
    elif group_by == 'thread':
        thread_name = event.thread_name
    elif group_by == 'type':
        type = event.type
    return IOTraceItem(0, type, 1, thread_name, '-', file_name, 
            event.page_count, event.duration, event.pages, event.sectors)

def accumulate(a, b):
    a.count += b.count
    a.page_count += b.page_count
    a.duration += b.duration
    a.pages += b.pages
    a.sectors += b.sectors

def group_list(group_by, list):
    if not group_by:
        return list

    groups = {}
    for item in list:
        key = group_key(group_by, item)
        if key not in groups:
            groups[key] = group_item(group_by, item)
            continue
        accumulate(groups[key], item)
        
    ret = []
    for key, val in groups.iteritems():
        ret.append(val)

    return ret

# type_filter: [R|W|S|B]
# filter: [file:<filename> | thread:<threadname>]
# group_by: [file | thread | type]
def trace2list(path, type_filter, filter, group_by):
    list = []
    events, files = TraceParser().parse_trace(path)
    data = defaultdict(lambda: defaultdict(int))
    for event in events:
        type = event.type

        if type_filter and type != type_filter:
            continue

        if not matches(event, filter):
            continue

        item = IOTraceItem(
            event.ts,
            event.type,
            1,
            event.thread_name,
            event.cpu,
            event.file_name,
            page_count(event),
            event.duration,
            pages(event),
            sectors(event)
        )
        list.append(item)

    return (group_list(group_by, list), files)

def parse_args():
    parser = argparse.ArgumentParser(description="I/O profile raw trace to list")
    parser.add_argument(
            '--input',
            required=True,
            dest='path',
            help='path to input file')
    parser.add_argument(
            '--type-filter',
            dest='type_filter',
            default=None,
            help='show only events of this type. possible values are R|W|S|B')
    parser.add_argument(
            '--filter',
            dest='filter',
            default=None,
            help='type+value to filter on. possible values are ' +
            'file:<filename>, thread:<thread-name>, type:<type>')
    parser.add_argument(
            '--group-by',
            dest='group_by',
            default=None,
            help='group by field. possible values are file | thread | type')
    parser.add_argument(
            '--hide-cols',
            dest='hidden_cols',
            default=['pages', 'sectors'],
            help='comma-separated list of column names to hide')
    parser.add_argument(
            '--list-cols',
            dest='list_cols',
            action='store_true',
            help='show all available column names')
    parser.add_argument(
            '--sort-col',
            dest='sort_col',
            default='timestamp',
            choices=['timestamp', 'count', 'page_count', 'duration'],
            help='column to sort by')

    return parser.parse_args()

def main():
    args = parse_args()
    
    if args.list_cols:
        print(IOTraceItem.all_col_names())
        return

    list, files = trace2list(args.path, args.type_filter, args.filter, args.group_by)

    list = sorted(list, key=lambda item: getattr(item, args.sort_col))

    # print legend
    print(IOTraceItem.col_names(args.hidden_cols))
    print('------------------------------------------------------------------')

    # print items
    for item in list:
        print(item.to_str(args.hidden_cols))

if __name__ == "__main__":
    main()
