/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import uniqueId from 'lodash/uniqueId';

export const ERROR = 'ERROR';
export const SUCCESS = 'SUCCESS';

/* Actions */
const ADD_GLOBAL_MESSAGE = 'ADD_GLOBAL_MESSAGE';

const addGlobalMessage = (message, level) => ({
  type: ADD_GLOBAL_MESSAGE,
  message,
  level
});

export const addGlobalErrorMessage = message =>
    addGlobalMessage(message, ERROR);

export const addGlobalSuccessMessage = message =>
    addGlobalMessage(message, SUCCESS);

/* Reducer */
const globalMessages = (state = [], action = {}) => {
  if (action.type === ADD_GLOBAL_MESSAGE) {
    return [{
      id: uniqueId('global-message-'),
      message: action.message,
      level: action.level
    }];
  }

  return state;
};

export default globalMessages;

/* Selectors */
export const getGlobalMessages = state => state;
