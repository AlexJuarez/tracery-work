// @flow

import type { CanvasContextProvider } from './CanvasContextProvider';

/**
 * Components that wish to live under a Surface should conform to this interface.
 */
export interface CanvasComponent {
  /**
   * Renders the entire component using the provided canvas context. Called by Surface from back to
   * front.
   */
  renderToCanvas(canvasContextProvider: CanvasContextProvider): void;
}
