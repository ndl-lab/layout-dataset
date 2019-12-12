export interface TargetImage{
version?:number;
id?:string;
binder?:string;
name?:string;
imageType?:string;
tags?:string[];
size?:ImageSize;
imagePath?:string;
note?:string;
bwThreshold?:number;
status?:Status;
annotations?:AnnotationObject[];
}
export interface ImageSize{
width?:number;
height?:number;
depth?:number;
}
export interface Bndbox{
xmin?:number;
ymin?:number;
xmax?:number;
ymax?:number;
}
export interface AnnotationObject{
id?:string;
name?:string;
bndbox?:Bndbox;
}
export type Status = "ESTIMATED"|"WORKING"|"CREATED"|"ANNOTATED"|"CHECKED";
