export class UserCredentials {

  id: number;
  firstname: string;
  lastname: string;
  email: string;
  role: string;
  password: string;

  constructor(id: number, firstname: string, lastname: string, email: string, role: string, password: string) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.role = role;
    this.password = password;
  }

}

