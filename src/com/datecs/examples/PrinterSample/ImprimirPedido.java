package com.datecs.examples.PrinterSample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;
import com.datecs.examples.PrinterSample.entity.Pedido;
import com.datecs.examples.PrinterSample.entity.Produto;
import com.datecs.examples.PrinterSample.network.PrinterServer;
import com.datecs.examples.PrinterSample.network.PrinterServerListener;

public class ImprimirPedido extends Activity {

	// Debug
	private static final String LOG_TAG = "Impressao";
	private static final boolean DEBUG = true;

	// Request to get the bluetooth device
	private static final int REQUEST_GET_DEVICE = 0;

	// Request to get the bluetooth device
	private static final int DEFAULT_NETWORK_PORT = 9100;

	// The listener for all printer events
	private final ProtocolAdapter.ChannelListener mChannelListener = new ProtocolAdapter.ChannelListener() {
		@Override
		public void onReadEncryptedCard() {
			toast(getString(R.string.msg_read_encrypted_card));
		}

		@Override
		public void onReadCard() {

		}

		@Override
		public void onReadBarcode() {

		}

		@Override
		public void onPaperReady(boolean state) {
			if (state) {
				toast(getString(R.string.msg_paper_ready));
			} else {
				toast(getString(R.string.msg_no_paper));
			}
		}

		@Override
		public void onOverHeated(boolean state) {
			if (state) {
				toast(getString(R.string.msg_overheated));
			}
		}

		@Override
		public void onLowBattery(boolean state) {
			if (state) {
				toast(getString(R.string.msg_low_battery));
			}
		}
	};

	// Member variables
	private Printer mPrinter;
	private ProtocolAdapter mProtocolAdapter;
	private PrinterInformation mPrinterInfo;
	private BluetoothSocket mBluetoothSocket;
	private PrinterServer mPrinterServer;
	private Socket mPrinterSocket;
	private boolean mRestart;
	private Pedido pedidoSelecionado = null;

	private List<Pedido> pedidos = new ArrayList<Pedido>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.impressao_tecnovendas);
		setTitle(getString(R.string.app_name));
		
		// botoes
		findViewById(R.id.btImprimir).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imprimir();
			}
		});

		findViewById(R.id.btImprimirUltimo).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						imprimirUltimo();
					}
				});

		mRestart = true;
		waitForConnection();
		pedidos.clear();
		
		buscaPedidos();
		
		Collections.reverse(pedidos);
		
		final ListView listview = (ListView) findViewById(R.id.listArquivos);
		
		listview.setAdapter(new ListViewAdapter(this, pedidos));
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				    Log.i("teste", pedidos.get(position).getProdutos().toString());
					pedidoSelecionado = pedidos.get(position);
					toast("Pedido "  + pedidoSelecionado.getCod_pe() + " selecionado.");
			}
		});
	}

	private void buscaPedidos() {
		BufferedReader leitor;
		String linha;
		String[] campos;
		Pedido pedido;
		List<Produto> produtos = new ArrayList<Produto>();
		Produto produto;
		Double valorTotal;
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/impressao");

		// procura todos os arquivos na pasta
		File[] files = f.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".txt");
			}
		});

		// percorre arquivos preenchendo os pedidos;
		for (int i = 0; i < files.length; ++i) {
			try {
				//reader = new FileReader(files[i].toString());
				//leitor = new BufferedReader(reader);
				leitor = new BufferedReader( new InputStreamReader(new FileInputStream(files[i].toString()), "ISO-8859-1"));

				
				
				// preenche cabecalho do pedido
				linha = leitor.readLine();
				if (linha != null) {
					pedido = new Pedido();
					valorTotal = 0D;
					
					campos = linha.split("\\|"); 
					// empresa|codigopedigo|datavenda|codigocliente|nomecliente|cidade|endereco|bairro|numero|pagamento|
					if (campos.length == 10) {
						pedido.setNomeEmpresa(campos[0]);
						pedido.setCod_pe(campos[1]);
						pedido.setDatavenda_pe(campos[2]);
						pedido.setCodcliente_pe(campos[3]);
						pedido.setCliente(campos[4]);
						pedido.setCidadeCliente(campos[5]);
						pedido.setEnderecoCliente(campos[6]);
						pedido.setBairroCliente(campos[7]);
						pedido.setNumeroCliente(campos[8]);
						pedido.setForpg_pe(campos[9]);
						
						produtos = new ArrayList<Produto>();
						// preenche detalhes do pedido
						// codigoProduto|descricao|qtde|unitario|valor total
						while ((linha = leitor.readLine()) != null) {
							campos = null;
							campos = linha.split("\\|");
							if (campos.length == 5) {
								produto = new Produto();
								produto.setCodproduto_pe(campos[0]);
								produto.setDescricao(campos[1]);
								produto.setQtde_pe(Double.valueOf(campos[2]));
								produto.setVrunit_pe(Double.valueOf(campos[3]));
								produto.setValor_pe(Double.valueOf(campos[4]));
								valorTotal+=produto.getValor_pe();
								produtos.add(produto);
								//Log.i("d", produto.toString());
							}
						}
						pedido.setProdutos(produtos);
						pedido.setValorTotal(valorTotal);
						pedidos.add(pedido);
					}
				}
				listaPedidos();
			} catch (FileNotFoundException e) {

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void listaPedidos() {
		for (Pedido pedido : pedidos) {
			//Log.i("Pedidos", pedidos.toString());
			//Log.i("Pedidos", pedido.getProdutos().toString());
		}
	}

	private String getRoot() {
		File root = android.os.Environment.getExternalStorageDirectory();
		return root.toString();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRestart = false;
		closeActiveConnection();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GET_DEVICE) {
			if (resultCode == DeviceListActivity.RESULT_OK) {
				String address = data
						.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// address = "192.168.11.136:9100";
				if (BluetoothAdapter.checkBluetoothAddress(address)) {
					establishBluetoothConnection(address);
				} else {
					establishNetworkConnection(address);
				}
			} else if (resultCode == RESULT_CANCELED) {

			} else {
				finish();
			}
		}
	}

	private void toast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isFinishing()) {
					Toast.makeText(getApplicationContext(), text,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void dialog(final int iconResId, final String title,
			final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ImprimirPedido.this);
				builder.setIcon(iconResId);
				builder.setTitle(title);
				builder.setMessage(msg);

				AlertDialog dlg = builder.create();
				dlg.show();
			}
		});
	}

	private void error(final String text, boolean resetConnection) {
		if (resetConnection) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), text,
							Toast.LENGTH_SHORT).show();
				}
			});

			waitForConnection();
		}
	}

	private void doJob(final Runnable job, final int resId) {
		// Start the job from main thread
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Progress dialog available due job execution
				final ProgressDialog dialog = new ProgressDialog(
						ImprimirPedido.this);
				dialog.setTitle(getString(R.string.title_please_wait));
				dialog.setMessage(getString(resId));
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();

				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							job.run();
						} finally {
							dialog.dismiss();
						}
					}
				});
				t.start();
			}
		});
	}

	protected void initPrinter(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);

		if (mProtocolAdapter.isProtocolEnabled()) {
			final ProtocolAdapter.Channel channel = mProtocolAdapter
					.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
			channel.setListener(mChannelListener);
			// Create new event pulling thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						try {
							channel.pullEvent();
						} catch (IOException e) {
							e.printStackTrace();
							error(e.getMessage(), mRestart);
							break;
						}
					}
				}
			}).start();
			mPrinter = new Printer(channel.getInputStream(),
					channel.getOutputStream());
		} else {
			mPrinter = new Printer(mProtocolAdapter.getRawInputStream(),
					mProtocolAdapter.getRawOutputStream());
		}

		mPrinterInfo = mPrinter.getInformation();

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) findViewById(R.id.edNomeImpressora))
						.setText(mPrinterInfo.getName());
			}
		});
	}

	public synchronized void waitForConnection() {
		closeActiveConnection();

		// Show dialog to select a Bluetooth device.
		startActivityForResult(new Intent(this, DeviceListActivity.class),
				REQUEST_GET_DEVICE);

		// Start server to listen for network connection.
		try {
			mPrinterServer = new PrinterServer(new PrinterServerListener() {
				@Override
				public void onConnect(Socket socket) {
					if (DEBUG)
						Log.d(LOG_TAG, "Accept connection from "
								+ socket.getRemoteSocketAddress().toString());

					// Close Bluetooth selection dialog
					finishActivity(REQUEST_GET_DEVICE);

					mPrinterSocket = socket;
					try {
						InputStream in = socket.getInputStream();
						OutputStream out = socket.getOutputStream();
						initPrinter(in, out);
					} catch (IOException e) {
						e.printStackTrace();
						error(getString(R.string.msg_failed_to_init) + ". "
								+ e.getMessage(), mRestart);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void establishBluetoothConnection(final String address) {
		closePrinterServer();

		doJob(new Runnable() {
			@Override
			public void run() {
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				BluetoothDevice device = adapter.getRemoteDevice(address);
				UUID uuid = UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB");
				InputStream in = null;
				OutputStream out = null;

				adapter.cancelDiscovery();

				try {
					if (DEBUG)
						Log.d(LOG_TAG, "Connect to " + device.getName());
					mBluetoothSocket = device
							.createRfcommSocketToServiceRecord(uuid);
					mBluetoothSocket.connect();
					in = mBluetoothSocket.getInputStream();
					out = mBluetoothSocket.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
					error(getString(R.string.msg_failed_to_connect) + ". "
							+ e.getMessage(), mRestart);
					return;
				}

				try {
					initPrinter(in, out);
				} catch (IOException e) {
					e.printStackTrace();
					error(getString(R.string.msg_failed_to_init) + ". "
							+ e.getMessage(), mRestart);
					return;
				}
			}
		}, R.string.msg_connecting);
	}

	private void establishNetworkConnection(final String address) {
		closePrinterServer();

		doJob(new Runnable() {
			@Override
			public void run() {
				Socket s = null;
				try {
					String[] url = address.split(":");
					int port = DEFAULT_NETWORK_PORT;

					try {
						if (url.length > 1) {
							port = Integer.parseInt(url[1]);
						}
					} catch (NumberFormatException e) {
					}

					s = new Socket(url[0], port);
					s.setKeepAlive(true);
					s.setTcpNoDelay(true);
				} catch (UnknownHostException e) {
					error(getString(R.string.msg_failed_to_connect) + ". "
							+ e.getMessage(), mRestart);
					return;
				} catch (IOException e) {
					error(getString(R.string.msg_failed_to_connect) + ". "
							+ e.getMessage(), mRestart);
					return;
				}

				InputStream in = null;
				OutputStream out = null;

				try {
					if (DEBUG)
						Log.d(LOG_TAG, "Connect to " + address);
					mPrinterSocket = s;
					in = mPrinterSocket.getInputStream();
					out = mPrinterSocket.getOutputStream();
				} catch (IOException e) {
					error(getString(R.string.msg_failed_to_connect) + ". "
							+ e.getMessage(), mRestart);
					return;
				}

				try {
					initPrinter(in, out);
				} catch (IOException e) {
					error(getString(R.string.msg_failed_to_init) + ". "
							+ e.getMessage(), mRestart);
					return;
				}
			}
		}, R.string.msg_connecting);
	}

	private synchronized void closeBlutoothConnection() {
		// Close Bluetooth connection
		BluetoothSocket s = mBluetoothSocket;
		mBluetoothSocket = null;
		if (s != null) {
			if (DEBUG)
				Log.d(LOG_TAG, "Close Blutooth socket");
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void closeNetworkConnection() {
		// Close network connection
		Socket s = mPrinterSocket;
		mPrinterSocket = null;
		if (s != null) {
			if (DEBUG)
				Log.d(LOG_TAG, "Close Network socket");
			try {
				s.shutdownInput();
				s.shutdownOutput();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void closePrinterServer() {
		closeNetworkConnection();

		// Close network server
		PrinterServer ps = mPrinterServer;
		mPrinterServer = null;
		if (ps != null) {
			if (DEBUG)
				Log.d(LOG_TAG, "Close Network server");
			try {
				ps.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void closePrinterConnection() {
		if (mPrinter != null) {
			mPrinter.release();
		}

		if (mProtocolAdapter != null) {
			mProtocolAdapter.release();
		}
	}

	private synchronized void closeActiveConnection() {
		closePrinterConnection();
		closeBlutoothConnection();
		closeNetworkConnection();
		closePrinterServer();
	}

	private void imprimirUltimo() {
		doJob(new Runnable() {
			@Override
			public void run() {
				if (pedidos.size() > 0){
					imprimePedido(pedidos.get(0));
				}
				
			}
		}, R.string.msg_printing_text);
	}

	public String geraLinha(String campo1, String campo2, int tamanho){
		String resultado = null;
		int completar = tamanho-(campo1.length() +campo2.length());
		if (completar > 0){
			resultado = campo1;
			
			for(int i = 0; i<completar; i++){
				resultado = resultado + " ";
			}
			resultado = resultado + campo2;
			
		}else if(completar == 0){
			resultado = campo1 + campo2;
		}else if(completar < 0){
			resultado = campo1 + campo2;
			resultado = resultado.substring(0, tamanho);
		}
		return resultado;
	}
	
	private String formataString(String campo, int tamanho){
		String resultado = null;
		int completar = tamanho-campo.length();
		if (completar > 0){
			resultado = campo;
			for(int i = 0; i<completar; i++){
				resultado = " " + resultado ;
			}  
		}else if(completar == 0){
			resultado = campo;
		}else if(completar < 0){
			resultado = campo.substring(0, tamanho);
		}
		
		return resultado;
	}
	
	
	private void imprimePedido(Pedido pedido) {
		//int tamanho = 48;
		int tamanho = 60;
		DecimalFormat df = new DecimalFormat("#####.00");
		
		StringBuffer sb = new StringBuffer();
		sb.append("{reset}{left}"+ geraLinha(pedido.getNomeEmpresa(), pedido.getDatavenda_pe(), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getCodcliente_pe() + "-" +pedido.getCliente(), "", tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getEnderecoCliente(), pedido.getNumeroCliente(), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getBairroCliente(), pedido.getCidadeCliente(), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha("-----------------------------------------------------------------------------------", "", tamanho)+ "{br}");
		for (Produto produto : pedido.getProdutos()){
			sb.append("{reset}{left}"+ formataString(""+(int)(produto.getQtde_pe()), 6) + geraLinha("   "+ produto.getCodproduto_pe() + "-" + produto.getDescricao(), "", tamanho-9)+ "{br}");
			sb.append("{reset}{left}"+ formataString(formataString(""+ df.format(Double.valueOf(produto.getVrunit_pe())), 15) + formataString(""+df.format(Double.valueOf(produto.getValor_pe())), 15),tamanho)+ "{br}");
		} 
		sb.append("{reset}{left}"+ geraLinha("-----------------------------------------------------------------------------------", "", tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getForpg_pe(), "Total: R$ " + df.format(Double.valueOf(pedido.getValorTotal())), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha("-----------------------------------------------------------------------------------", "", tamanho)+ "{br}");
		/*sb.append("{reset}{left}"+ geraLinha(pedido.getNomeEmpresa(), pedido.getDatavenda_pe(), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getCodcliente_pe() + "-" +pedido.getCliente(), "", tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getEnderecoCliente(), pedido.getNumeroCliente(), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha(pedido.getBairroCliente(), pedido.getCidadeCliente(), tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha("-----------------------------------------------------------------------------------", "", tamanho)+ "{br}");
		sb.append("{reset}{left}"+ formataString("Valor total: R$ " + df.format(Double.valueOf(pedido.getValorTotal())),tamanho)+ "{br}");
		sb.append("{reset}{left}"+ geraLinha("-----------------------------------------------------------------------------------", "", tamanho)+ "{br}");
		sb.append("{br}"); */
		sb.append("{reset}{left}"+ geraLinha("", "Assinatura: _______________________________________________________________________", tamanho)+ "{br}");
		
		/*sb.append("{reset}RM DISTRIBUIDORA{right}" + pedido.getDatavenda_pe()
				+ "{br}");
		sb.append("{reset}" + pedido.getCliente() + "{br}");
		sb.append("{reset}" + pedido.getEnderecoCliente() + "-"
				+ pedido.getCidadeCliente() + "{br}");
		sb.append("{reset}--------------------------------------------------{br}");
		// sb.append("{reset}" + pedido.getNitem_pe() + ".{u}"+
		// pedido.getCodproduto_pe() +"{br}");
		/*
		 * sb.append("{reset}{center}{w}{h}RECEIPT"); sb.append("{br}");
		 * sb.append("{br}"); 
		 * sb.append("{reset}1. {b}First item{br}");
		 * sb.append("{reset}{right}{h}$0.50 A{br}");
		 * sb.append("{reset}2. {u}Second item{br}");
		 * sb.append("{reset}{right}{h}$1.00 B{br}");
		 * sb.append("{reset}3. {i}Third item{br}");
		 * sb.append("{reset}{right}{h}$1.50 C{br}"); sb.append("{br}");
		 * sb.append("{reset}{right}{w}{h}TOTAL: {/w}$3.00  {br}");
		 * sb.append("{br}"); sb.append("{reset}{center}{s}Thank You!{br}");
		 */

		try {
			if (DEBUG)
				Log.d(LOG_TAG, "Print Text");
			mPrinter.reset();
			mPrinter.printTaggedText(sb.toString());
			mPrinter.feedPaper(110);
			mPrinter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			error(getString(R.string.msg_failed_to_print_text) + ". "
					+ e.getMessage(), mRestart);
		}
	}

	private void imprimir() {
		doJob(new Runnable() {
			@Override
			public void run() {
				if (pedidoSelecionado != null){
					toast("Imprimindo pedido "  + pedidoSelecionado.getCod_pe());
					imprimePedido(pedidoSelecionado);
				}else
					toast("Selecione um pedido.");
			}
		}, R.string.msg_printing_text);
	}

}
