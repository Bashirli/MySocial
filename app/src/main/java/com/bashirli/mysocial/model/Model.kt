package com.bashirli.mysocial.model

import com.google.firebase.Timestamp
import java.io.Serializable
class Model(var email:String,var nickname:String
            ,var text:String,var viewCount:String,
            var userPP:String?,var image:String?,var time:String
            ,var id:String) : Serializable{
}