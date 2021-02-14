using System;
using System.IO;
using System.Threading;

namespace Server
{
    public class Observer
    {
        class CommitThread
        {
            string _output = null;

            string _msg = null;

            public CommitThread(string output, string msg)
            {
                _msg = msg;
                _output = output;
            }

            public void Invoke()
            {
                Console
                    .WriteLine("Observer ThreadID:" +
                    Thread.CurrentThread.ManagedThreadId);
                File.WriteAllText (_output, _msg);
            }
        }

        CommandLine.Parsed<Options> _comandArgs;

        public Observer(CommandLine.Parsed<Options> args)
        {
            _comandArgs = args;
        }

        public void commit(object sender, string msg)
        {
            if (_comandArgs.Value.Output != null)
            {
                CommitThread func =
                    new CommitThread(_comandArgs.Value.Output, msg);
                Thread thread = new Thread(new ThreadStart(func.Invoke));
                thread.Start();
            }
        }
    }
}
