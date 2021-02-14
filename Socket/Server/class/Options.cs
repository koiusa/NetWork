using System;
using CommandLine;
namespace Server
{
    public class Options
    {
        [
            CommandLine.Option(
                'o',
                "output",
                Required = false,
                HelpText = "FilePath For Output")
        ]
        public string Output { get; set; }

        [
            CommandLine.Option(
                'p',
                "port",
                Required = false,
                HelpText = "Port For SocketServer")
        ]
        public string Port { get; set; }
    }
}
