// @flow

import React from 'react';

export type ListItem = {
  label: string,
  key: any,
}

type Props = {
  items: Array<ListItem>,
}

type DefaultProps = {
  onItemClicked: (key: any, event: SyntheticMouseEvent) => boolean,
}

class List extends React.Component {
  static defaultProps: DefaultProps;

  props: Props & DefaultProps;

  renderListItems(): Array<React.Element<*>> {
    return this.props.items.map(
      (item: ListItem): React.Element<*> => (
        <li
          key={item.key}
        >
          <a
            href="#"
            onClick={(event: SyntheticMouseEvent): boolean =>
              this.props.onItemClicked(item.key, event)}
          >
          {item.label}
          </a>
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
