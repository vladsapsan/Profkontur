package com.android.profkontur

class Person{
    var name: String = "";
    var SecondName: String = "";

     constructor(Name:String,SName:String){
         this.name = Name;
         this.SecondName = SName;
     }
    fun Fio():String{
        return this.name+" "+this.SecondName
    }
}

fun main(){
    var moment: Int
    println("kokojambo")
}

