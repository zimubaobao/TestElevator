
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class TestElevator
{
	public static void main(String[] args)
	{
		SimpleFrame frame = new SimpleFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //�˳�ģʽ���˳�Ӧ�ó�����Ĭ�ϴ��ڹرղ���
		frame.setLocation(40, 40);    //����λ������
		frame.show();
	}
}

class SimpleFrame extends JFrame
{
	public SimpleFrame()
	{
		setSize(690, 510);
		setTitle("����ģ��");
		SimplePanel panel = new SimplePanel();
		this.add(panel);
	}
}

class SimplePanel extends JPanel
{
	private CanvasOuter myCanvasOuter;//��ʾ�ⲿ��ť�Ļ����࣬λ�����
	private CanvasInner myCanvasInner;//��ʾ�ڲ���ť�Ļ����࣬λ���Ҳ�

	private Elevator[] s;//��������
	private Found foundThread = new Found();//��������߳�

	public final static int NUM = 2; //������
	public final static int FLOOR = 6; //¥��
	
	final int UP = 0; //����״̬��ʼ��
	final int DOWN = 1;
	final int PAUSE = 2;
	final int OPEN = 3;
	final int CLOSE = 4;

	public SimplePanel()
	{
		setLayout(null);
		myCanvasOuter = new CanvasOuter();
		myCanvasInner = new CanvasInner();
		add(myCanvasOuter);
		add(myCanvasInner);

		s = new Elevator[NUM];
		for (int i = 0; i < s.length; i++)
		{
			s[i] = new Elevator(i);
		}
		for (int i = 0; i < s.length; i++)
		{
			s[i].start();//
		}
		foundThread.start();
	}

	class Found extends Thread //��������߳�
	{
		private ArrayList tasks;
		private Task task;
		public Found()
		{
			tasks = new ArrayList();
		}
		public void addTask(int i, boolean flag)
		{
			tasks.add(new Task(i, flag));
		}
		public void run()
		{
			while (true)
			{
				if (!tasks.isEmpty())
				{
					task = (Task) (tasks.get(0));
					int i = task.floor;
					int minFloor = FLOOR;
					int id = 0;
					boolean f = task.isUp;
					boolean isFound = false;
					if (f)//Ҫ��¥
					{
						for (int j = 0; j < NUM; j++)//������
						{
							if ((s[j].state == PAUSE) || (s[j].current < i && s[j].state == UP))//ͣ���ߵ������ڴ�������
							{
								int distance = Math.abs(i - s[j].current);
								if (distance < minFloor)
								{
									id = j;
									minFloor = distance;
									isFound = true;
								}
							}
						}
					}
					else//��¥
					{
						for (int j = 0; j < NUM; j++)
						{
							if ((s[j].state == PAUSE) || (s[j].current > i && s[j].state == DOWN))//ͣ�����ڴ�������
							{
								int distance = Math.abs(i - s[j].current);
								if (distance < minFloor)
								{
									id = j;
									minFloor = distance;
									isFound = true;
								}
							}

						}
					}
					if (isFound)
					{
						s[id].addFloor(i);
						tasks.remove(0);
					}                                                                                                                                                                                                                              
				}
				try
				{
					sleep(100);
				}
				catch (InterruptedException e)
				{
					System.out.println("Interrupted");
				}
			}
		}
		class Task//����Ҫ����¥����ȥ������ȥ
		{
			private int floor;
			private boolean isUp;
			public Task(int i, boolean flag)
			{
				floor = i;
				isUp = flag;
			}
		}
	}
	public void resetOuterB(int floor) //�����ⲿ�İ�ť
	{
		myCanvasOuter.bUp[floor].setEnabled(true);
		myCanvasOuter.bDown[floor].setEnabled(true);
		myCanvasOuter.bUp[floor].setForeground(null);
		myCanvasOuter.bDown[floor].setForeground(null);
	}

	public void drawButtons(int id)//�����ڲ���ť
	{
		for (int i = 0; i < FLOOR; i++)
		{
			if (s[id].destinations.contains(new Integer(i)))
			{
				myCanvasInner.interPanels[id].interB[i].setBackground(Color.YELLOW);
			}
			else
			{
				myCanvasInner.interPanels[id].interB[i].setBackground(null);
			}
		}
	}

	class Elevator extends Thread //��������
	{ 
		private int id; //���ݱ�ʶ�� 
		private int floor = FLOOR; //�ܲ���
		private JPanel myPanel = new JPanel();
		private JPanel myPanel1 = new JPanel();
		private JButton numB;
		private JButton stateB = new JButton("---");
		private JButton[] buttons; //�������
		public int current = 1; //����¥��
		public int state = PAUSE; //�����б�־
		private JButton floorB = new JButton("1");

		private ArrayList destinations; //Ŀ�ĵ�����

		public Elevator(int x)
		{
			id = x;
			myPanel1.setLayout(new GridLayout());
			numB = new JButton("��"+(x+1)+"�ŵ���");
			myPanel1.add(numB);
			myPanel.setLayout(new GridLayout(floor + 2, 1));
			floorB.setBackground(Color.WHITE);
			myPanel.add(floorB);
			myPanel.add(stateB);

			buttons = new JButton[FLOOR];
			for (int i = buttons.length - 1; i >= 0; i--)
			{
				buttons[i] = new JButton();
				buttons[i].setBackground(Color.LIGHT_GRAY);
				myPanel.add(buttons[i]);
			}
			buttons[0].setBackground(Color.RED);
			myPanel1.setBounds(100 * id + 250, 10, 100, 50);
			myPanel.setBounds(100 * id + 250, 60, 100, 400);
			add(myPanel1);
			add(myPanel);
			
			destinations = new ArrayList();
		}

		public void addFloor(int i) //��Ӧ�ⲿ��ť
		{
			if (destinations.contains(new Integer(i)))
				return;
			if (state == PAUSE)
			{
				destinations.add(new Integer(i));
				buttons[i].setBackground(Color.YELLOW);
				if (current > i)
				{
					state = DOWN;
				}
				else
				{
					state = UP;
				}
				return;
			}

			if (state == UP)
			{
				for (int j = 0; j < destinations.size(); j++)
				{
					if (i < ((Integer) (destinations.get(j))).intValue())
					{
						destinations.add(j, new Integer(i));
						buttons[i].setBackground(Color.YELLOW);
					}
				}
			}

			if (state == DOWN)
			{
				for (int j = 0; j < destinations.size(); j++)
				{
					if (i > ((Integer) (destinations.get(j))).intValue())
					{
						destinations.add(j, new Integer(i));
						buttons[i].setBackground(Color.YELLOW);
					}
				}
			}
			destinations.add(new Integer(i));
			buttons[i].setBackground(Color.YELLOW);
		}

		public void wantToFloor(int i) //��Ӧ�ڲ���ť
		{
			if (destinations.contains(new Integer(i)))
				return;
			if (state == PAUSE)
			{
				destinations.add(new Integer(i));
				buttons[i].setBackground(Color.YELLOW);
				if (current > i + 1)
				{
					state = DOWN;
				}
				else
				{
					state = UP;
				}
				return;
			}

			if (state == UP)
			{
				if (current > i + 1)
					return;
				for (int j = 0; j < destinations.size(); j++)
				{
					if (i < ((Integer) (destinations.get(j))).intValue())
					{
						destinations.add(j, new Integer(i));
						buttons[i].setBackground(Color.YELLOW);
						return;
					}
				}
			}
			if (state == DOWN)
			{
				if (current < i)
					return;
				for (int j = 0; j < destinations.size(); j++)
				{
					if (i > ((Integer) (destinations.get(j))).intValue())
					{
						destinations.add(j, new Integer(i));
						buttons[i].setBackground(Color.YELLOW);
						return;
					}
				}
			}
			destinations.add(new Integer(i));
			buttons[i].setBackground(Color.YELLOW);
		}

		public void setStateB() //��������״̬��ť
		{
			if (state == PAUSE)
			{
				stateB.setText("---");
				stateB.setForeground(null);
				
				myCanvasInner.interPanels[id].stateB.setText("---");
				myCanvasInner.interPanels[id].stateB.setForeground(null);
				
				myCanvasOuter.stateB1.setText("---");
				myCanvasOuter.stateB1.setForeground(null);
				
				myCanvasOuter.stateB2.setText("---");
				myCanvasOuter.stateB2.setForeground(null);
			}
			else if (state == UP)
			{
				stateB.setText("��");
				stateB.setForeground(Color.RED);
				
				myCanvasInner.interPanels[id].stateB.setText("��");
				myCanvasInner.interPanels[id].stateB.setForeground(Color.RED);
				
				if(id==0)
				{
					myCanvasOuter.stateB1.setText("��");
					myCanvasOuter.stateB1.setForeground(Color.RED);
				}
				if(id==1)
				{
					myCanvasOuter.stateB2.setText("��");
					myCanvasOuter.stateB2.setForeground(Color.RED);
				}
			}
			else
			{
				stateB.setText("��");
				stateB.setForeground(Color.RED);
				
				myCanvasInner.interPanels[id].stateB.setText("��");
				myCanvasInner.interPanels[id].stateB.setForeground(Color.RED);
				
				if(id==0)
				{
					myCanvasOuter.stateB1.setText("��");
					myCanvasOuter.stateB1.setForeground(Color.RED);
				}
				if(id==1)
				{
					myCanvasOuter.stateB2.setText("��");
					myCanvasOuter.stateB2.setForeground(Color.RED);
				}
			}
		}

		public void run()
		{
			while (true)
			{
				if (state != PAUSE)       //working
				{
					if (state == OPEN)
					{
						buttons[(current + FLOOR - 1) % FLOOR].setBackground(Color.BLACK);
						try
						{
							sleep(2000);
						}
						catch (InterruptedException e)
						{
							System.out.println("Interrupted");
						}
						buttons[(current + FLOOR - 1) % FLOOR].setBackground(Color.RED);
						state = PAUSE;
					}
					else
					{
						int i =((Integer) (destinations.get(0))).intValue() + 1;
						//��ǰ����¥��
						if (current == i)  //����һ��¥��
						{
							destinations.remove(0);
							if (destinations.isEmpty())
							{
								state = PAUSE;
							}
							setStateB();
							drawButtons(id);
							buttons[(current + FLOOR - 1) % FLOOR].setBackground(Color.BLACK);
							try
							{
								sleep(2000);
							}
							catch (InterruptedException e)
							{
								System.out.println("Interrupted");
							}
							buttons[(current + FLOOR - 1) % FLOOR].setBackground(Color.RED);
							resetOuterB(current - 1);
						}
						else
						{
							int follow = current; //����ǰ��¥��
							if (state == UP)
							{
								current++;
							}
							else
							{
								current--;
							}
							floorB.setText(Integer.toString(current));
							floorB.setForeground(Color.green);
							myCanvasInner.interPanels[id].floorB.setText(Integer.toString(current));
							myCanvasInner.interPanels[id].floorB.setForeground(Color.green);
							
							if(id==0)
							{
								myCanvasOuter.floorB1.setText(Integer.toString(current));
								myCanvasOuter.floorB1.setForeground(Color.green);
							}
							if(id==1)
							{
								myCanvasOuter.floorB2.setText(Integer.toString(current));
								myCanvasOuter.floorB2.setForeground(Color.green);
							}
							
							buttons[(current + FLOOR - 1) % FLOOR].setBackground(Color.RED);
							buttons[(follow + FLOOR - 1) % FLOOR].setBackground(Color.LIGHT_GRAY);
							setStateB();
						}
					}
				}
				try
				{
					sleep(1000);
				}
				catch (InterruptedException e)
				{
					System.out.println("Interrupted");
				}
			}
		}
	}

	class CanvasOuter extends JPanel //��ʾ�ⲿ��ť�Ļ����࣬λ�����
	{
		private JPanel[] oneFloor;//¥�㻭��
		private int num = SimplePanel.FLOOR;
		private JButton[] bFloor; //¥��
		private JPanel jp;
		
		private JPanel[] updownpanel;//��ʾ���°�ť
		private JButton[] bUp; //���а�ť
		private JButton[] bDown; //���а�ť
		private JPanel outp;//��ʾ�������ⲿ��

		private JPanel statePanel1;//��ʾ1�ź�2�ŵ��ݵ�״̬
		private JPanel statePanel2;
		private JButton state1;
		private JButton state2;
		private JButton floorB1;//��ʾ1�ŵ���¥�㰴ť
		private JButton stateB1;//��ʾ1�ŵ����ϡ��°�ť
		private JButton floorB2;//��ʾ2�ŵ���¥�㰴ť
		private JButton stateB2;//��ʾ2�ŵ����ϡ��°�ť
		CanvasOuter()
		{
			setLayout(null);
			
			jp = new JPanel(new GridLayout(8,1));
			jp.setBounds(20,160,225,400);
			
			outp=new JPanel(new GridLayout());
			JButton out = new JButton("�����ⲿ");
			outp.setBounds(20, 10, 225, 60);
			outp.add(out);
			
			bFloor = new JButton[num];//�������鳤��
			bUp = new JButton[num];
			bDown = new JButton[num];
			oneFloor = new JPanel[num];
			updownpanel = new JPanel[num];
			
			statePanel1 = new JPanel(new GridLayout(3,1));
			statePanel1.setBounds(20, 70, 110, 90);
			state1 = new JButton("һ�ŵ���");
			floorB1 = new JButton("1");//��ʾ1�ŵ���¥�㰴ť
			stateB1 = new JButton("---");//��ʾ1�ŵ����ϡ��°�ť
			floorB1.setBackground(Color.WHITE);
			statePanel2 = new JPanel(new GridLayout(3,1));
			statePanel2.setBounds(134, 70, 110, 90);
			state2 = new JButton("���ŵ���");
			floorB2 = new JButton("1");//��ʾ2�ŵ���¥�㰴ť
			stateB2 = new JButton("---");//��ʾ2�ŵ����ϡ��°�ť
			floorB2.setBackground(Color.WHITE);
			
			statePanel1.add(state1);
			statePanel1.add(floorB1);
			statePanel1.add(stateB1);
			statePanel2.add(state2);
			statePanel2.add(floorB2);
			statePanel2.add(stateB2);
			
			for(int i=num-1;i>=0;i--)
			{
				updownpanel[i] = new JPanel(new GridLayout(2,1));
				bFloor[i] = new JButton(i+1+"¥");
				bUp[i] = new JButton();
				if (i != num - 1)
				{
					bUp[i].setText("��");
					bUp[i].addActionListener(new UpAction(i));
				}
				bDown[i] = new JButton();
				if (i != 0)
				{
					bDown[i].setText("��");
					bDown[i].addActionListener(new UpAction(i));
				}
				updownpanel[i].add(bUp[i]);
				updownpanel[i].add(bDown[i]);
				oneFloor[i]= new JPanel(new GridLayout(1,2));//
				oneFloor[i].add(bFloor[i]);
				oneFloor[i].add(updownpanel[i]);
				
				jp.add(oneFloor[i]);
			}
			add(jp);
			add(statePanel1);
			add(statePanel2);
			add(outp);
			this.setBounds(0, 0, 250, 550);
			}
		
		class DownAction implements ActionListener
		{
			private int floor;
			public DownAction(int i)
			{
				floor = i;
			}
			public void actionPerformed(ActionEvent e)
			{
				foundThread.addTask(floor, true);
			}
		}

		class UpAction implements ActionListener
		{
			private int floor;
			public UpAction(int i)
			{
				floor = i;
			}
			public void actionPerformed(ActionEvent e)
			{
				foundThread.addTask(floor, true);
			}
		}
		
	}

	class CanvasInner extends JPanel //��ʾ�ڲ���ť�Ļ�����λ���Ҳ�
	{
		private int num = NUM;
		private InterButton[] interPanels;
		public final static int WIDTH = 200;
		private JPanel panel2 = new JPanel();
		private JButton inviewB = new JButton("�����ڲ�");
		public CanvasInner()
		{
			this.setLayout(null);
			interPanels = new InterButton[num];

			panel2.setLayout(new GridLayout());
			panel2.add(inviewB);
			panel2.setBounds(0, 0, 200, 60);
			add(panel2);
			
			for (int i = 0; i < num; i++)
			{
				interPanels[i] = new InterButton(i);
				add(interPanels[i]);
			}
			setBounds(455, 10, WIDTH, 450);
		}

		class InterButton extends JPanel //��ʾÿ�������ڲ���ť����
		{
			private int id; //���ݱ�ʶ��
			private JPanel panel = new JPanel();
			private JPanel panel1 = new JPanel();
			private JButton numB;
			private JButton floorB = new JButton("1");
			private JButton stateB = new JButton("---");
			private JButton openB = new JButton("<>");
			private JButton closeB = new JButton("><");
			private JButton[] interB; //�ڲ���ť����

			public InterButton(int j)
			{
				id = j;
				this.setLayout(null);
				numB = new JButton("��"+(j+1)+"�ŵ���");
				int width = CanvasInner.WIDTH;
				floorB.setBackground(Color.WHITE);
				
				panel1.setLayout(new GridLayout());
				panel1.add(numB);
				panel1.setBounds(0, 60, width, 40);
				
				panel.setLayout(new GridLayout(5, 2));
				panel.add(stateB);
				panel.add(floorB);
				panel.setBounds(0, 100, width, 150);
				
				interB = new JButton[FLOOR];
				for (int i = 0; i < FLOOR; i++)
				{
					interB[i] = new JButton();
					interB[i].setText(i+1+"¥");
					panel.add(interB[i]);
					interB[i].addActionListener(new goFloor(i));
				}
				
				panel.add(openB);
				panel.add(closeB);
				openB.addActionListener(new OpenAction());
				closeB.addActionListener(new CloseAction());
				
				this.add(panel);
				this.add(panel1);
				this.setBounds(0, j * 200, width, 260);
			}

			class goFloor implements ActionListener
			{
				private int floor;    //Ŀ�ĵ�¥��
				public goFloor(int i)
				{
					floor = i;
				}
				public void actionPerformed(ActionEvent e)
				{
					interB[floor].setBackground(Color.YELLOW);
					s[id].wantToFloor(floor);
				}
			}
			
			class OpenAction implements ActionListener  //���ż���Ӧ������
			{
				public void actionPerformed(ActionEvent e)
				{
					if (s[id].state == PAUSE)
					{
						s[id].state = OPEN;
					}
				}
			}
			
			class CloseAction implements ActionListener //���ż���Ӧ������
			{
				public void actionPerformed(ActionEvent e)
				{
					int i = s[id].current - 1;
					s[id].buttons[i].setBackground(Color.RED);
				}
			}
		}
	}
}
