// @flow

export const SUCCESS = 'Success';
export const FAILURE = 'Failure';
export const IN_PROGRESS = 'In Progress';

export type StatusCode = string;

export type FetchStatus = {
  code: StatusCode,
  message?: string,
}
