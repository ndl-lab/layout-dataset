export interface Action{
type?:Status;
note?:string;
user?:string;
start?:number;
end?:number;
}
export type Status = "ESTIMATED"|"WORKING"|"CREATED"|"ANNOTATED"|"CHECKED";
