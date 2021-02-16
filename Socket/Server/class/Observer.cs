using System;
using System.Collections.Generic;
using System.IO;
using System.Threading;

namespace Server
{
    public class Observer
    {
        class CommitThread : IThreadPoolWorkItem
        {
            private string _output = null;

            private Queue<string> task = new Queue<string>();

            private Mutex mut = new Mutex();

            private bool signal = false;

            public CommitThread(string output)
            {
                _output = output;
            }
            public void add(string msg)
            {
                task.Enqueue(msg);
            }

            public void Execute()
            {
                while (signal)
                {
                    mut.WaitOne();
                    if (task.Count > 0)
                    {
                        string msg = task.Dequeue();
                        if (!string.Empty.Equals(msg))
                        {
                            Console
                           .WriteLine("Observer ThreadID:" +
                           Thread.CurrentThread.ManagedThreadId);
                            File.WriteAllText(_output, msg);
                        }
                    }
                    mut.ReleaseMutex();
                }
            }

            public void stop()
            {
                signal = false;
                task.Clear();
                mut.Close();
            }
        }

        private CommandLine.Parsed<Options> _comandArgs;

        CommitThread thread = null;

        public Observer(CommandLine.Parsed<Options> args)
        {
            _comandArgs = args;
        }

        public void start()
        {
            thread = new CommitThread(_comandArgs.Value.Output);
            thread.Execute();
        }

        public void stop()
        {
            if (thread != null)
            {
                thread.stop();
            }
            
        }

        public void commit(object sender, string msg)
        {
            if (_comandArgs.Value.Output != null)
            {
                thread.add(msg);
            }
        }
    }
}
