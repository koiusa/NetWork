using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace Server
{
    class App
    {
        static void Main(string[] args)
        {
            try
            {
                CommandLine.Parsed<Options> result =
                    CommandLine.Parser.Default.ParseArguments<Options>(args) as
                    CommandLine.Parsed<Options>;
                if (result != null)
                {
                    if (result.Value.Port != null)
                    {
                        Console.WriteLine("Port: {0}", result.Value.Port);
                    }
                    if (result.Value.Output != null)
                    {
                        Console.WriteLine("Output: {0}", result.Value.Output);
                    }
                    Console
                        .WriteLine("Main ThreadID:" +
                        Thread.CurrentThread.ManagedThreadId);
                    SocketServer prog =
                        new SocketServer(!String.IsNullOrEmpty(result.Value.Port)
                                ? result.Value.Port
                                : Constants.DEFAULT_TCP_IP_PORT.ToString());
                    prog.init();
                    prog.config(result);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}
