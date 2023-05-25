package com.example.travelerv2

class Travel {


    var id: String?=null
    var name :String?=null
    var descrition: String?=null
    var diameter: Double?=0.0
    var file_name: String? = null
    var lat: Double?=0.0
    var lng: Double?=0.0
    constructor(){}

    constructor(id:String?,name:String?,descrition:String?,diameter:Double?,file_name:String?,lat:Double?,lng:Double?){
        this.id=id
        this.name=name
        this.descrition=descrition
        this.diameter=diameter
        this.file_name= file_name
        this.lat = lat
        this.lng = lng

    }

    override fun toString(): String {
        return "Travel(" + this.id + ","+ this.name +", " + this.file_name+")"
    }
}