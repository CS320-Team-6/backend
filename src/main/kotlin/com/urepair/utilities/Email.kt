package com.urepair.utilities

// import com.amazonaws.regions.Regions
// import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
// import com.amazonaws.services.simpleemail.model.Body
// import com.amazonaws.services.simpleemail.model.Content
// import com.amazonaws.services.simpleemail.model.Destination
// import com.amazonaws.services.simpleemail.model.Message
// import com.amazonaws.services.simpleemail.model.SendEmailRequest
//
// val awsRegion = Regions.US_EAST_2
//
// fun sendEmail(to: String, subject: String, message: String) {
//    val ses = AmazonSimpleEmailServiceClientBuilder.standard()
//        .withRegion(awsRegion)
//        .build()
//
//    val params = SendEmailRequest()
//        .withDestination(Destination().withToAddresses(to))
//        .withMessage(
//            Message()
//                .withBody(Body().withText(Content().withData(message)))
//                .withSubject(Content().withData(subject)),
//        )
//        .withSource("staff@urepair.me")
//
//    try {
//        val result = ses.sendEmail(params)
//        println("Email sent: $result")
//    } catch (error: Exception) {
//        println("Error sending email: $error")
//    }
// }
