package com.example.travelerv2

class Location {



    var name :String?=null
    var lat: Double?=0.0
    var lng: Double?=0.0

    constructor(){}

    constructor(name:String?,lat:Double?,lng:Double?){

        this.name=name
        this.lat=lat
        this.lng=lng

    }
}