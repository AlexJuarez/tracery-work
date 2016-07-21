// @flow

export type Action<Payload> = {
  type: string,
  payload: Payload,
  error?: boolean,
}

export default function createAction<Payload>(
  type: string,
  payload: Payload,
  error?: boolean): Action<Payload> {
  return {
    type,
    payload,
    error,
  };
}
