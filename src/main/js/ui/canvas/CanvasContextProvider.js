// @flow

import type { CanvasComponent } from './CanvasComponent';

/**
 * This interface is a hack to help us use Flow to force components that will be contained within a
 * Surface to adhere to a certain interface. Flow (as of 0.28) cannot properly typecheck children of
 * a React component.
 */
export interface CanvasContextProvider {
  /** `CanvasComponent`s should pass `this` as the parameter to obtain an appropriate context. */
  getCanvasContext(component: CanvasComponent): CanvasRenderingContext2D;
}
