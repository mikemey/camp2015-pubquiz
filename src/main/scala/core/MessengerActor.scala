package core

import akka.actor.{Actor, Props}

object MessengerActor {


  case class SendMessage(messageType: String, to: String, message: String)

}

class MessengerActor extends Actor {

  import EmailActor._
  import MessengerActor._
  import SMSActor._

  val email = context.actorOf(Props[EmailActor])
  val sms = context.actorOf(Props[SMSActor])

  def receive: Receive = {
    case SendMessage("email", to, message) => email ! SendEmail(to, message)
    case SendMessage("sms", to, message) => sms ! SendSMS(to, message)
  }
}
