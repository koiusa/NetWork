using System;
using System.Diagnostics;
using System.Threading;
using System.Windows;

namespace Client
{
    public partial class MainWindow : Window
    {
        private void setIpComboBoxItems(TcpInfo info)
        {
            tInfo.receptionActiveConnections();
            ip_comboBox.ItemsSource = tInfo.endPoints;
            ip_comboBox.SelectedValuePath = "Key";
            ip_comboBox.DisplayMemberPath = "Key";
            ip_comboBox.SelectedIndex = 0;
        }

        /** 接続ボタンを押して接続処理 **/
        private void btn_Connect_Click(object sender, EventArgs e)
        {
            Debug
                .WriteLine("btn_Connect_Click" +
                " ThreadID:" +
                Thread.CurrentThread.ManagedThreadId);
            try
            {
                //接続先ホスト名
                string host = ip_comboBox.Text;

                //接続先ポート
                int port = int.Parse(port_textBox.Text);

                //接続処理
                // Connect to the remote endpoint.
                tClient.Connect (host, port);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void btn_Send_Click(object sender, EventArgs e)
        {
            if (!tClient.IsClosed)
            {
                //送信
                tClient.Send(message_textBox.Text);
            }
        }
    }
}
