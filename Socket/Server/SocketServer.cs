﻿using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace Server
{
    class SocketServer
    {
        //データ受信イベント
        public delegate void ReceiveEventHandler(object sender, string e);
        public event ReceiveEventHandler OnReceiveData;

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

        private static ManualResetEvent
            SocketEvent = new ManualResetEvent(false);

        private IPEndPoint ipEndPoint;

        private Socket sock;

        private Thread tMain;

        private Observer observer;

        SocketServer(string port)
        {
            Console
                .WriteLine("Program ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            IPAddress myIP =
                Dns
                    .GetHostEntry(Dns.GetHostName())
                    .AddressList
                    .Where(address =>
                        address.AddressFamily == AddressFamily.InterNetwork)
                    .ToArray()[0];
            ipEndPoint = new IPEndPoint(myIP, Int32.Parse(port));
        }

        void config(CommandLine.Parsed<Options> args)
        {
            observer = new Observer(args);
            //データ受信イベント
            OnReceiveData +=
                new ReceiveEventHandler(observer.commit);
        }

        void init()
        {
            Console
                .WriteLine("init ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            sock =
                new Socket(AddressFamily.InterNetwork,
                    SocketType.Stream,
                    ProtocolType.Tcp);
            sock.Bind (ipEndPoint);
            sock.Listen(10);
            Console.WriteLine("サーバー起動中・・・");
            tMain = new Thread(new ThreadStart(Round));
            tMain.Start();
        }

        void Round()
        {
            Console
                .WriteLine("Round ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            while (true)
            {
                SocketEvent.Reset();
                sock.BeginAccept(new AsyncCallback(OnConnectRequest), sock);
                SocketEvent.WaitOne();
            }
        }

        void OnConnectRequest(IAsyncResult ar)
        {
            Console
                .WriteLine("OnConnectRequest ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            SocketEvent.Set();
            Socket listener = (Socket) ar.AsyncState;
            Socket handler = listener.EndAccept(ar);
            Console.WriteLine(handler.RemoteEndPoint.ToString() + " joined");
            StateObject state = new StateObject();
            state.workSocket = handler;
            handler
                .BeginReceive(state.buffer,
                0,
                StateObject.BUFFER_SIZE,
                0,
                new AsyncCallback(ReadCallback),
                state);
        }

        void ReadCallback(IAsyncResult ar)
        {
            Console
                .WriteLine("ReadCallback ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            StateObject state = (StateObject) ar.AsyncState;
            Socket handler = state.workSocket;
            int ReadSize = handler.EndReceive(ar);
            if (ReadSize < 1)
            {
                Console
                    .WriteLine(handler.RemoteEndPoint.ToString() +
                    " disconnected");
                return;
            }
            byte[] bb = new byte[ReadSize];
            Array.Copy(state.buffer, bb, ReadSize);
            string msg = System.Text.Encoding.UTF8.GetString(bb);
            Console.WriteLine (msg);
            OnReceiveData(this, msg);
            byte[] res = System.Text.Encoding.UTF8.GetBytes("from server response\r\n");
            handler
                .BeginSend(res,
                0,
                res.Length,
                0,
                new AsyncCallback(WriteCallback),
                state);
        }

        void WriteCallback(IAsyncResult ar)
        {
            Console
                .WriteLine("WriteCallback ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            StateObject state = (StateObject) ar.AsyncState;
            Socket handler = state.workSocket;
            handler.EndSend (ar);
            Console.WriteLine("送信完了");
            handler
                .BeginReceive(state.buffer,
                0,
                StateObject.BUFFER_SIZE,
                0,
                new AsyncCallback(ReadCallback),
                state);
        }

        void disConnect()
        {
            Console
                .WriteLine("disConnect ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            sock.Close();
        }
    }
}
