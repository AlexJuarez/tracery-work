// @flow

import type { FileInfo } from './FileInfo';
import type { TraceInfo } from './TraceInfo';

export type GetTracesResult = Array<TraceInfo>;
export type GetFilesResult = Array<FileInfo>;

/**
 * IMPORTANT: Keep in sync with tracery.thrift.

 * TODO: It would be nice if Thrift could generate this for us. It does have built-in support for
 * TypeScript; we could either fork that or write a dumb translator.
 */
export type TraceryService = {
  getTraces: (callback: (result: GetTracesResult) => void) => GetTracesResult;
  getFiles: (callback: (result: GetFilesResult) => void) => GetFilesResult;
}
