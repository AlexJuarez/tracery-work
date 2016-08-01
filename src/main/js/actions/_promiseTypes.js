// @flow

export type PromiseResolveFunction<Result> =
  (result: Promise<Result> | Result) => void;

export type PromiseRejectFunction<Error> = (error: Error) => void;
