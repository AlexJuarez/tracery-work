// @flow

/**
 * IMPORTANT: Keep in sync with tracery.thrift.

 * TODO: It would be nice if Thrift could generate this for us. It does have built-in support for
 * TypeScript; we could either fork that or write a dumb translator.
 */
export type FileInfo = {
  traceId: string,
  fileName: string,
  fileSize: number,
  inode: number,
}
