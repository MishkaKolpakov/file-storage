export class FileMetadata {

  resourceId: number;
  ownerId: number;
  fileUUID: string;
  expirationTime: string;
  fileSize: number;
  fileName: string;
  mime: string;
  permission: PERMISSION_TYPE_ENUM;
  key: string;

  constructor(resourceId: number, ownerId: number,
              fileUUID: string, expirationTime: string,
              fileSize: number, fileName: string, mime: string,
              permission: PERMISSION_TYPE_ENUM, key: string) {
    this.resourceId = resourceId;
    this.ownerId = ownerId;
    this.fileUUID = fileUUID;
    this.expirationTime = expirationTime;
    this.fileSize = fileSize;
    this.fileName = fileName;
    this.mime = mime;
    this.permission = permission;
    this.key = key;
  }
}

export enum PERMISSION_TYPE_ENUM {
  ALL_USERS = 'ALL_USERS', LIST_OF_USERS = 'LIST_OF_USERS'
}
