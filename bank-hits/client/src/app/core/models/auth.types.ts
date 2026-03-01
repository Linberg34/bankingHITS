export interface LoginRequest {
  email: string;
}

export interface LoginResponse {
  token: string;
}

export interface UserMe {
  id: number;
  name: string;
  email: string;
  status: string;
  registeredAt: string;
  role: string;
}
