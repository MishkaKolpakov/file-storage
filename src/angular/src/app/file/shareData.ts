import {FileMetadata} from './fileMetadata';
export class ShareData {

  fileMetadata: FileMetadata;
  permittedUserIds: number[];

  constructor(fileMetadata: FileMetadata, permittedUserIds: number[]) {
    this.fileMetadata = fileMetadata;
    this.permittedUserIds = permittedUserIds;
  }
}

