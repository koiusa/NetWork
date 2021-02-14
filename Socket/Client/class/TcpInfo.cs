using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.NetworkInformation;
using System.Net.Sockets;

namespace Client
{
    public class TcpInfo
    {
        public enum TcpType
        {
            local,
            remote
        }

        public Dictionary<string, TcpType> endPoints { get; set; }

        public List<int> usedPorts { get; set; }

        public TcpInfo()
        {
            endPoints = new Dictionary<string, TcpType>();
            usedPorts = new List<int>();
        }

        public void getIpTable()
        {
            IPGlobalProperties ipprop =
                IPGlobalProperties.GetIPGlobalProperties();
            Console.WriteLine("ホスト名:{0}", ipprop.HostName);
            Console.WriteLine("ドメイン名:{0}", ipprop.DomainName);
            Console.WriteLine("DHCPスコープ名:{0}", ipprop.DhcpScopeName);
            Console.WriteLine("WINSプロキシか:{0}", ipprop.IsWinsProxy);
            Console.WriteLine("NetBIOSノードタイプ:{0}", ipprop.NodeType);

            //TcpConnectionInformationの配列を取得
            TcpConnectionInformation[] infos = ipprop.GetActiveTcpConnections();

            //TCP接続に関する情報を列挙する
            foreach (System.Net.NetworkInformation.TcpConnectionInformation
                info
                in
                infos
            )
            {
                Console.WriteLine("ローカルアドレス:{0}", info.LocalEndPoint);
                Console.WriteLine("リモートアドレス:{0}", info.RemoteEndPoint);
                Console.WriteLine("状態:{0}", info.State);
            }
        }

        public void receptionActiveConnections()
        {
            IPGlobalProperties ipprop =
                IPGlobalProperties.GetIPGlobalProperties();
            TcpConnectionInformation[] infos = ipprop.GetActiveTcpConnections();
            endPoints.Clear();
            endPoints
                .Add(Dns
                    .GetHostEntry(ipprop.HostName)
                    .AddressList
                    .Where(ip => ip.AddressFamily == AddressFamily.InterNetwork)
                    .ToArray()[0]
                    .ToString(),
                TcpType.local);
            foreach (IPAddress
                address
                in
                infos
                    .Where(ip =>
                        ip.State == TcpState.Established &&
                        ip.RemoteEndPoint.Address.AddressFamily ==
                        AddressFamily.InterNetwork)
                    .Select(ip => ip.RemoteEndPoint.Address)
                    .ToArray()
            )
            {
                if (!endPoints.ContainsKey(address.ToString()))
                {
                    endPoints.Add(address.ToString(), TcpType.remote);
                }
            }
        }

        public void receptionUsedPorts()
        {
            IPGlobalProperties ipprop =
                IPGlobalProperties.GetIPGlobalProperties();
            TcpConnectionInformation[] infos = ipprop.GetActiveTcpConnections();
            usedPorts.Clear();
            foreach (int
                port
                in
                infos
                    .Where(ip => ip.State == TcpState.Established)
                    .Select(ip => ip.LocalEndPoint.Port)
            )
            {
                if (!usedPorts.Contains(port))
                {
                    usedPorts.Add (port);
                }
            }
        }
    }
}
