package com.example.news

import android.net.Uri

class Users {
    lateinit var name:String
    lateinit var status:String
    lateinit var image:String

    //Default constructor required for calls to
    //DataSnapshot.getValue(User.class)
    constructor(){

    }

    constructor(name:String,status:String,image:String){
        this.name=name
        this.status=status
        this.image = image
    }


}