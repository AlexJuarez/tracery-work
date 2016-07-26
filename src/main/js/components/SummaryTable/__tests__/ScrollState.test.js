jest.unmock('../records/ScrollState');
jest.unmock('../constants');

import ScrollState from '../records/ScrollState';
import { SCROLL_DIRECTIONS } from '../constants';

describe('ScrollState', () => {
  let scrollState;
  beforeEach(() => {
    scrollState = new ScrollState();
  });

  function updateOffset(offset, direction) {
    const newScrollState = scrollState.setOffset(offset);
    expect(newScrollState.direction).toEqual(direction);
    return newScrollState;
  }

  it('should update direction down', () => {
    const newScrollState = updateOffset(50, SCROLL_DIRECTIONS.DOWN);
    expect(newScrollState).not.toEqual(scrollState);
  });

  it('should update the direction up', () => {
    scrollState = updateOffset(50, SCROLL_DIRECTIONS.DOWN);
    const newScrollState = updateOffset(25, SCROLL_DIRECTIONS.UP);
    expect(newScrollState).not.toEqual(scrollState);
  });

  it('should be equal if offset is equal', () => {
    const newScrollState = updateOffset(0, SCROLL_DIRECTIONS.DOWN);
    expect(newScrollState).toEqual(scrollState);
  });
});
