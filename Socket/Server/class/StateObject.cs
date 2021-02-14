using System.Text;
using System.Net.Sockets;

namespace Server
{
    public class StateObject
    {
        public Socket workSocket { get; set; }

        public const int BUFFER_SIZE = 1024;

        internal byte[] buffer = new byte[BUFFER_SIZE];

        public StringBuilder sb = new StringBuilder();
    }
}
