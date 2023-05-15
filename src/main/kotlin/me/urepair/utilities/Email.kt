package me.urepair.utilities

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest

fun sendEmail(to: String, subject: String, htmlBody: String) {
    val ses = AmazonSimpleEmailServiceClientBuilder.standard()
        .build()

    val params = SendEmailRequest()
        .withDestination(Destination().withToAddresses(to))
        .withMessage(
            Message()
                .withBody(Body().withHtml(Content().withData(htmlBody)))
                .withSubject(Content().withData(subject)),
        )
        .withSource("staff@urepair.me")

    try {
        val result = ses.sendEmail(params)
        println("Email sent: $result")
    } catch (error: Exception) {
        println("Error sending email: $error")
    }
}
