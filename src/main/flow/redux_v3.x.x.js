// @flow
// Adapted from https://github.com/flowtype/flow-typed. License reproduced below:
//
// The MIT License (MIT)
//
// Copyright (c) 2015 Murphy Randle
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


declare module 'redux' {
  declare type State = any;
  declare type Action = Object;
  declare type AsyncAction = any;
  declare type Reducer<S, A> = (state: S, action: A) => S;
  declare type BaseDispatch = (a: Action) => Action;
  declare type Dispatch = (a: Action | AsyncAction) => any;
  declare type ActionCreator = (...args: any) => Action | AsyncAction;
  declare type MiddlewareAPI = { dispatch: Dispatch, getState: () => State };
  declare type Middleware = (api: MiddlewareAPI) => (next: Dispatch) => Dispatch;
  declare type Store = {
    dispatch: Dispatch,
    getState: () => State,
    subscribe: (listener: () => void) => () => void,
    replaceReducer: (reducer: Reducer<any, any>) => void
  };
  declare type StoreCreator = (reducer: Reducer<any, any>, initialState: ?State) => Store;
  declare type StoreEnhancer = (next: StoreCreator) => StoreCreator;
  declare type ActionCreatorOrObjectOfACs = ActionCreator | { [key: string]: ActionCreator };
  declare type Reducers = { [key: string]: Reducer<any, any> };
  declare class Redux {
    bindActionCreators<actionCreators: ActionCreatorOrObjectOfACs>(
      actionCreators: actionCreators, dispatch: Dispatch): actionCreators;
    combineReducers(reducers: Reducers): Reducer<any, any>;
    createStore(reducer: Reducer<any, any>, initialState?: State, enhancer?: StoreEnhancer): Store;
    applyMiddleware(...middlewares: Array<Middleware>): StoreEnhancer;
    compose(...functions: Array<Function | StoreEnhancer>): Function;
  }
  declare var exports: Redux;
}
