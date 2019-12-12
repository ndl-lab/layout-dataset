export interface ImageBinder{
id?:string;
version?:number;
name?:string;
imageType?:string;
tags?:string[];
status?:Status;
seeAlso?:string;
images?:number;
holder?:string;
imagePath?:string;
actions?:Action[];
}
export interface Action{
type?:Status;
note?:string;
user?:string;
start?:number;
end?:number;
}
export type Status = "ESTIMATED"|"WORKING"|"CREATED"|"ANNOTATED"|"CHECKED";
