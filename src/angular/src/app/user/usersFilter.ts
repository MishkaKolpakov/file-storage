export class UsersFilter {
  offset: number;
  limit: number;
  order: ORDER_ENUM;
  sortField: USER_FIELD_ENUM;
  role: ROLE_ENUM;
  userStatus: USER_STATUS_ENUM;
  passwordStatus: PASSWORD_STATUS_ENUM;

  constructor(offset: number, limit: number,
              order: ORDER_ENUM, sortField: USER_FIELD_ENUM,
              role: ROLE_ENUM,
              userStatus: USER_STATUS_ENUM,
              passwordStatus: PASSWORD_STATUS_ENUM) {
    this.offset = offset;
    this.limit = limit;
    this.order = order;
    this.sortField = sortField;
    this.role = role;
    this.userStatus = userStatus;
    this.passwordStatus = passwordStatus;
  }

}

export enum ORDER_ENUM {
  ASC = 'ASC', DESC = 'DESC'
}

export enum ROLE_ENUM {
  USER = 'USER', TECH_SUPP = 'TECH_SUPP', ADMIN = 'ADMIN'
}

export enum USER_STATUS_ENUM {
  ACTIVE = 'ACTIVE', DELETED = 'DELETED'
}

export enum PASSWORD_STATUS_ENUM {
  ACTIVE = 'ACTIVE', EXPIRED = 'EXPIRED'
}

export enum USER_FIELD_ENUM {
  id = 'id', firstname = 'firstname', lastname = 'lastname', email = 'email', role = 'role'
}

