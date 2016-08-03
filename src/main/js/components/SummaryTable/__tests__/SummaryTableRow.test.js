jest.unmock('../SummaryTableRow');

import SummaryTableRow from '../SummaryTableRow';
import React from 'react';
import { shallow } from 'enzyme';
import { List } from 'immutable';

describe('<SummaryTableRow />', () => {
  let wrapper;

  function setupTableRow(props) {
    wrapper = shallow(<SummaryTableRow id="row" {...props} />);
  }

  describe('Loading State', () => {
    beforeEach(() => {
      setupTableRow({ data: new List(), rowNumber: 1, height: 22 });
    });

    it('should have only one cell', () => {
      expect(wrapper.find('td').length).toEqual(1);
    });

    it('should be striped', () => {
      expect(wrapper.hasClass('summary-table-row-striped')).toBeTruthy();
    });
  });

  describe('Loaded State', () => {
    const data = new List([0, 'test', 'test2']);
    beforeEach(() => {
      setupTableRow({ data, rowNumber: 0, height: 22 });
    });

    it('should have children', () => {
      expect(wrapper.children().length).toEqual(data.size);
    });

    it('should not be striped', () => {
      expect(wrapper.hasClass('summary-table-row-striped')).toBeFalsy();
    });
  });
});
