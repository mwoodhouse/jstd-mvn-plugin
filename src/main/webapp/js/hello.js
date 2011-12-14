function hello(name)
{
    //pausecomp(5000)

    return "Hello" + name;
}

function pausecomp(ms)
{
    ms += new Date().getTime();
    while (new Date() < ms)
    {
    }
}