export interface User {
    id: number;
    username: string;
    created_at: string; //timestamp
    profile_url: string;
    locX: number;
    locY: number;
    message: string;
    messaged_at: string; //timestamp
}