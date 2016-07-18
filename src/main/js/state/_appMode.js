// @flow

import type { AppMode } from './appMode';
import * as appModes from './appMode';

export default function appMode(state: AppMode = appModes.STARTUP, action: any): AppMode {
  switch (action.type) {
    default:
      return state;
  }
}
