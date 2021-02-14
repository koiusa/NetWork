using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading;
using System.Windows;

namespace Client
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //Socketクライアント
        TcpClient tClient = new TcpClient();

        TcpInfo tInfo = new TcpInfo();

        public MainWindow()
        {
            Debug
                .WriteLine("MainWindow" +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            InitializeComponent();

            base.Closing += this.MainWindow_Closing;

            //接続OKイベント
            tClient.OnConnected +=
                new TcpClient.ConnectedEventHandler(tClient_OnConnected);

            //接続断イベント
            tClient.OnDisconnected +=
                new TcpClient.DisconnectedEventHandler(tClient_OnDisconnected);

            //データ受信イベント
            tClient.OnReceiveData +=
                new TcpClient.ReceiveEventHandler(tClient_OnReceiveData);

            setIpComboBoxItems (tInfo);
            tInfo.receptionUsedPorts();
        }

        /** 接続断イベント **/
        void tClient_OnDisconnected(object sender, EventArgs e)
        {
            Debug
                .WriteLine("tClient_OnDisconnected" +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            
            if (!((TcpClient)sender).IsClosed)
                Dispatcher
                    .Invoke(new DisconnectedDelegate(Disconnected),
                    new object[] { sender, e });
        }

        delegate void DisconnectedDelegate(object sender, EventArgs e);

        private void Disconnected(object sender, EventArgs e)
        {
            //接続断処理
            Debug
                .WriteLine("Disconnected" +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
        }

        /** 接続OKイベント **/
        void tClient_OnConnected(EventArgs e)
        {
            //接続OK処理
            Debug
                .WriteLine("tClient_OnConnected" +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
        }

        /** データ受信イベント **/
        void tClient_OnReceiveData(object sender, string e)
        {
            Debug
                .WriteLine("tClient_OnReceiveData" +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            //別スレッドからくるのでInvokeを使用
            if (!((TcpClient)sender).IsClosed)
                Dispatcher
                    .Invoke(new ReceiveDelegate(ReceiveData),
                    new object[] { sender, e });
        }

        delegate void ReceiveDelegate(object sender, string e);

        //データ受信処理
        private void ReceiveData(object sender, string e)
        {
            Debug
                .WriteLine("ReceiveData:" +
                e +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            receive_text.Text = e;
        }

        private void MainWindow_Closing(object sender, CancelEventArgs e)
        {
            // If data is dirty, notify user and ask for a response
            string msg = "Data is dirty. Close without saving?";
            MessageBoxResult result =
                MessageBox
                    .Show(msg,
                    "Data App",
                    MessageBoxButton.YesNo,
                    MessageBoxImage.Warning);
            if (result == MessageBoxResult.No)
            {
                // If user doesn't want to close, cancel closure
                e.Cancel = true;
            }
            else
            {
                if (!tClient.IsClosed) tClient.Close();
            }
        }
    }
}
