jest.unmock('../src/Hello');

import { shallow } from 'enzyme';
import React from 'react';
import Hello from '../src/Hello';

describe('Hello', () => {
  it('returns "Hello World"', () => {
    const result = shallow(<Hello />);

    expect(result.text()).toEqual('Hello, world!');
  });
});
