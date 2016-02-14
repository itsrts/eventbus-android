# EventBus
This is a single file event bus library for android. It has no dependencies. Just copy paste the file and use it.

Here are some code samples to use it : ('receiver' is the reference to the subscriber)
<code>
EventManager.getInstance().subscribe(receiver, "term"); // subscribe to a term
EventManager.getInstance().subscribe(receiver, "term1", "term2", "term3"); // subscribe to a multiple terms
</code>

Make sure to unsubscribe from the event bus when you don't plan to receive the updates.
<code>
EventManager.getInstance().unSubscribe(receiver, "term"); // un-subscribe from a term
EventManager.getInstance().unSubscribe(receiver); // un-subscribe from all terms
</code>

UnSubscribe all receiver for some terms.

<code>
EventManager.getInstance().unSubscribeAll("term"); // un-subscribe all from this term
EventManager.getInstance().unSubscribeAll("term1", "term2", "term3"); // un-subscribe all from these terms
</code>

How to receive the callback. Implement the EventReceiver interface and override the 'onEvent' method.
The onEvent callback is called from the same thread from where you subscribed to the event bus.
So you are all clear to do everything on the UI thread and make no complications with handlers or something else.

<code>
@Override
    public void onEvent(String term, Object object) {
        //do anything on the UI thread
    }
</code>

You can also create polymorphic onEvent methods for receiving a known object.
For example, If you know that you will be receiving a 'String' object. You can create the following method.

<code>
@Override
    public void onEvent(String term, String object) {
        //do anything on the UI thread
    }
</code>

In this case, the <code>onEvent(String term, Object object)</code> will not be called, only <code>onEvent(String term, String object)</code> will be invoked.

And last, thanks for the open source community for always.
