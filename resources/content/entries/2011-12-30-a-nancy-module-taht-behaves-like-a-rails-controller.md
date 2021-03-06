---
date: 2011-12-30T00:00:00Z
title: A Nancy Module that Behaves like a Rails Controller... Mother of God
published: true
categories: [.NET]
type: article
external: false
---
If [Nancy](http://nancyfx.org) is inspired by Sinatra I thought I'd have a little fun a create an abstract class that created modules that kind of mimic the behaviour of Rails controllers.  Code first, ask questions later...

```csharp 
public abstract class RailslikeControllerFor<TEntity> : NancyModule where TEntity : class
{
    public RailslikeControllerFor() : base("/" + typeof(TEntity).Name + "s")
    {
        Get[@"/"] = Index;
        Get[@"/new"] = New;
        Post[@"/"] = Create;
        Get[@"/(?<id>[\d])"] = Show;
        Get[@"/(?<id>[\d])/edit"] = Edit;
        Put[@"/(?<id>[\d])"] = Update;
        Delete[@"/(?<id>[\d])"] = Destroy;
    }

    protected virtual Nancy.Response Index(dynamic context) { return 404; }
    protected virtual Nancy.Response New(dynamic context) { return 404; }
    protected virtual Nancy.Response Create(dynamic context) { return 404; }
    protected virtual Nancy.Response Show(dynamic context) { return 404; }
    protected virtual Nancy.Response Edit(dynamic context) { return 404; }
    protected virtual Nancy.Response Update(dynamic context) { return 404; }
    protected virtual Nancy.Response Destroy(dynamic context) { return 404; }

    protected Nancy.Response AsView(object model = null)
    {
        string method = new StackTrace().GetFrame(1).GetMethod().Name;
        string entity = typeof(TEntity).Name;

        return View[string.Format("{0}/{1}", entity, method), model];
    }
}
```

To use it you just subclass the class and override the necessary methods.  Like so.

```csharp 
public class QuestionModule : RailslikeControllerFor<Question>
{
    IQuestionsRepository questions;

    public QuestionModule(IQuestionsRepository questions)
        : base() { this.questions = questions; }

    protected override Response Index(dynamic context)
    {
        return AsView(questions.GetAll());
    }
}
```

What the base class does is create the necessary CRUDdy urls with a base route starting with the name of the entity you pass in, pluralised in the easiest way possible.  In this case `/questions`.  The rules match what you wold get for a controller in Rails.

<table style="width:100%" border="1">
    <tbody>
        <tr style="background-color: #666;color:#fff;">
            <th style="padding:4px;font-weight:bold;">Verb </th>
            <th style="padding:4px;font-weight:bold;">Path</th>
            <th style="padding:4px;font-weight:bold;">Action </th>
            <th style="padding:4px;font-weight:bold;"></th>
        </tr>
        <tr>
            <td style="padding:4px;"><span class="caps">GET</span>     </td>
            <td style="padding:4px;">/questions           </td>
            <td style="padding:4px;">index    </td>
            <td style="padding:4px;">display a list of all questions</td>
        </tr>
        <tr style="background-color: #fff;">
            <td style="padding:4px;"><span class="caps">GET</span>     </td>
            <td style="padding:4px;">/questions/new       </td>
            <td style="padding:4px;">new      </td>
            <td style="padding:4px;">return an <span class="caps">HTML</span> form for creating a new question</td>
        </tr>
        <tr>
            <td style="padding:4px;"><span class="caps">POST</span>    </td>
            <td style="padding:4px;">/questions           </td>
            <td style="padding:4px;">create   </td>
            <td style="padding:4px;">create a new question</td>
        </tr>
        <tr style="background-color: #fff;">
            <td style="padding:4px;"><span class="caps">GET</span>     </td>
            <td style="padding:4px;">/questions/:id       </td>
            <td style="padding:4px;">show     </td>
            <td style="padding:4px;">display a specific question</td>
        </tr>
        <tr>
            <td style="padding:4px;"><span class="caps">GET</span>     </td>
            <td style="padding:4px;">/questions/:id/edit  </td>
            <td style="padding:4px;">edit     </td>
            <td style="padding:4px;">return an <span class="caps">HTML</span> form for editing a question</td>
        </tr>
        <tr style="background-color: #fff;">
            <td style="padding:4px;"><span class="caps">PUT</span>     </td>
            <td style="padding:4px;">/questions/:id       </td>
            <td style="padding:4px;">update   </td>
            <td style="padding:4px;">update a specific question</td>
        </tr>
        <tr>
            <td style="padding:4px;"><span class="caps">DELETE</span>  </td>
            <td style="padding:4px;">/questions/:id       </td>
            <td style="padding:4px;">destroy  </td>
            <td style="padding:4px;">delete a specific question</td>
        </tr>
    </tbody>
</table>
<br/>

There is a teeny-tiny bit of sugar in the `AsView` method that will look up the corresponding view.  So the module above would resolve its `Index` method to `questions/index` and the view engine would find the correct file (eg. for Razor - `questions/index.cshtml`).

I've already said it's a bit of fun but I have actually used this at least once - there is probably more that could be added but it'll do for a first spike at least.  Any use to anyone?