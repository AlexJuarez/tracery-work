// @flow

import React from 'react';

type Props = {
  items: Array<string>,
}

type DefaultProps = {
  onItemClicked: (item: string, event: SyntheticMouseEvent) => boolean,
}

class List extends React.Component {
  static defaultProps: DefaultProps;

  props: Props & DefaultProps;

  renderListItems(): Array<React.Element<*>> {
    return this.props.items.map(
      (item: string): React.Element<*> => (
        <li
          key={item}
          onClick={(event: SyntheticMouseEvent): boolean =>
            this.props.onItemClicked(item, event)}
        >
          {item}
        </li>
      )
    );
  }

  render(): React.Element<*> {
    return (
      <ul>
        {this.renderListItems()}
      </ul>
    );
  }
}
List.defaultProps = {
  onItemClicked: (): boolean => true,
};

export default List;
