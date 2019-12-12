export interface ImageType{
id?:string;
version?:number;
name?:string;
types?:AnnotationType[];
}
export interface AnnotationType{
id?:string;
name?:string;
description?:string;
large?:boolean;
}
