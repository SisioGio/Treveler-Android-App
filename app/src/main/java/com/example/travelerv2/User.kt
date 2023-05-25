package com.example.travelerv2

class User {


    var email : String? = null
    var uid : String? = null

    constructor(){}

    constructor(email:String?,uid:String?){

        this.email=email
        this.uid=uid
    }
}