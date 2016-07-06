from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from __future__ import unicode_literals

from collections import defaultdict
import sys
import re
import logging

class Logger:
    @staticmethod
    def info(msg):
        logging.info(msg)

    @staticmethod
    def warning(msg):
        logging.warning(msg)

    @staticmethod
    def error(msg):
        logging.error(msg)

    @staticmethod
    def enableInfo():
        logging.basicConfig(level=logging.INFO)

    @staticmethod
    def enableWarning():
        logging.basicConfig(level=logging.WARNING)

    @staticmethod
    def enableError():
        logging.basicConfig(level=logging.ERROR)

def die(msg, ret=1):
    Logger.error(msg)
    sys.exit(ret)
