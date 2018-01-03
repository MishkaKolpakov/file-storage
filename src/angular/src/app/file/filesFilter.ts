export class FilesFilter {
  offset: number;
  limit: number;
  order: ORDER_ENUM;
  sortField: METADATA_FIELD_ENUM;
  fileType: FILE_TYPE;
  userId: number;
  permissionType: PERMISSION_TYPE_ENUM;

  constructor(offset: number, limit: number, order: ORDER_ENUM, sortField: METADATA_FIELD_ENUM, fileType: FILE_TYPE, userId: number, permissionType: PERMISSION_TYPE_ENUM) {
    this.offset = offset;
    this.limit = limit;
    this.order = order;
    this.sortField = sortField;
    this.fileType = fileType;
    this.userId = userId;
    this.permissionType = permissionType;
  }
}

export enum ORDER_ENUM {
  ASC = 'ASC', DESC = 'DESC'
}

export enum FILE_TYPE {
  OWN = 'OWN', SHARED = 'SHARED'
}

export enum METADATA_FIELD_ENUM {
  resourceId = 'resourceId', fileName = 'fileName', expirationTime = 'expirationTime'
}

export enum PERMISSION_TYPE_ENUM {
  resourceId = 'resourceId', fileName = 'fileName', expirationTime = 'expirationTime'
}
