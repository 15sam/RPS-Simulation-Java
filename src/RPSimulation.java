import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Rock-Paper-Scissors Simulation
 * - Entities move randomly and bounce off walls
 * - On collision: winner converts loser into winner's type
 * - Top-left shows counts
 *
 * Single-file demo using Swing (Java 8+)
 */
public class RPSimulation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("RPS Swarm Simulation");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setResizable(false);

            GamePanel panel = new GamePanel(900, 720);
            f.add(panel, BorderLayout.CENTER);

            JPanel controls = new JPanel();
            JButton startBtn = new JButton("Start");
            JButton pauseBtn = new JButton("Pause");
            JButton resetBtn = new JButton("Reset");
            JButton addBtn = new JButton("Add 10 Random");
            controls.add(startBtn);
            controls.add(pauseBtn);
            controls.add(resetBtn);
            controls.add(addBtn);

            startBtn.addActionListener(e -> panel.start());
            pauseBtn.addActionListener(e -> panel.pause());
            resetBtn.addActionListener(e -> panel.reset());
            addBtn.addActionListener(e -> panel.addRandomEntities(10));

            f.add(controls, BorderLayout.SOUTH);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);

            panel.reset(); // start with initial set
            panel.start();
        });
    }

    enum Type { ROCK, PAPER, SCISSORS }

    static class Entity {
        double x, y;
        double vx, vy;
        double radius;
        Type type;
        Color color;

        Entity(double x, double y, double vx, double vy, double radius, Type type) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
            this.radius = radius; this.type = type;
            this.color = colorFor(type);
        }

        void updateColor() { this.color = colorFor(type); }

        private static Color colorFor(Type t) {
            switch (t) {
                case ROCK: return new Color(120, 120, 120); // grey
                case PAPER: return new Color(244, 242, 232); // off-white
                case SCISSORS: return new Color(220, 40, 90); // red-ish
            }
            return Color.BLACK;
        }
    }

    @SuppressWarnings("serial")
    static class GamePanel extends JPanel {
        final int width, height;
        final List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
        final Random rand = new Random();
        javax.swing.Timer timer;

        final int fps = 60;

        GamePanel(int w, int h) {
            this.width = w; this.height = h;
            setPreferredSize(new Dimension(width, height));
            setBackground(Color.WHITE);

            int delay = 1000 / fps;
            timer = new javax.swing.Timer(delay, e -> gameLoop());


            // mouse to add single entity by click
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    Type t = Type.values()[rand.nextInt(3)];
                    addEntityAt(e.getX(), e.getY(), t);
                }
            });
        }

        void start() { if (!timer.isRunning()) timer.start(); }
        void pause() { if (timer.isRunning()) timer.stop(); }
        void reset() {
            pause();
            entities.clear();
            // initial distribution: a few of each, like screenshot-ish
            addRandomEntities(18, Type.PAPER);
            addRandomEntities(15, Type.SCISSORS);
            addRandomEntities(2, Type.ROCK);
            repaint();
        }

        void addRandomEntities(int n) {
            for (int i = 0; i < n; i++) {
                Type t = Type.values()[rand.nextInt(3)];
                addEntityRandom(t);
            }
        }

        void addRandomEntities(int n, Type t) {
            for (int i = 0; i < n; i++) addEntityRandom(t);
        }

        void addEntityRandom(Type t) {
            double r = 14 + rand.nextDouble() * 8;
            double x = r + rand.nextDouble() * (width - 2 * r);
            double y = r + rand.nextDouble() * (height - 2 * r);
            double speed = 0.6 + rand.nextDouble() * 1.6;
            double angle = rand.nextDouble() * Math.PI * 2;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            entities.add(new Entity(x, y, vx, vy, r, t));
        }

        void addEntityAt(int x, int y, Type t) {
            double r = 14 + rand.nextDouble() * 8;
            double speed = 0.6 + rand.nextDouble() * 1.6;
            double angle = rand.nextDouble() * Math.PI * 2;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            entities.add(new Entity(x, y, vx, vy, r, t));
            repaint();
        }

        void gameLoop() {
            moveEntities();
            detectCollisionsAndResolve();
            repaint();
        }

        void moveEntities() {
            synchronized (entities) {
                for (Entity en : entities) {
                    en.x += en.vx;
                    en.y += en.vy;
                    // bounce off walls
                    if (en.x - en.radius < 0) { en.x = en.radius; en.vx *= -1; }
                    if (en.x + en.radius > width) { en.x = width - en.radius; en.vx *= -1; }
                    if (en.y - en.radius < 0) { en.y = en.radius; en.vy *= -1; }
                    if (en.y + en.radius > height) { en.y = height - en.radius; en.vy *= -1; }
                }
            }
        }

        void detectCollisionsAndResolve() {
            // naive O(n^2) collision check — fine for a few hundred entities
            synchronized (entities) {
                int n = entities.size();
                for (int i = 0; i < n; i++) {
                    Entity a = entities.get(i);
                    for (int j = i + 1; j < n; j++) {
                        Entity b = entities.get(j);
                        double dx = a.x - b.x;
                        double dy = a.y - b.y;
                        double dist2 = dx * dx + dy * dy;
                        double minDist = a.radius + b.radius;

                        if (dist2 <= minDist * minDist) {
                            // they collided
                            Type winner = winnerOf(a.type, b.type);
                            if (winner == null) {
                                // same type -> simple elastic-ish response
                                bounceEntities(a, b);
                            } else {
                                // convert loser into winner
                                if (winner == a.type && winner != b.type) {
                                    b.type = a.type; b.updateColor();
                                    randomizeVelocity(b);
                                } else if (winner == b.type && winner != a.type) {
                                    a.type = b.type; a.updateColor();
                                    randomizeVelocity(a);
                                }
                                // also give a small bounce so they separate
                                bounceEntities(a, b);
                            }
                        }
                    }
                }
            }
        }

        // returns null when tie (same type), otherwise returns winning Type
        static Type winnerOf(Type a, Type b) {
            if (a == b) return null;
            // rock beats scissors
            if (a == Type.ROCK && b == Type.SCISSORS) return a;
            if (b == Type.ROCK && a == Type.SCISSORS) return b;
            // scissors beats paper
            if (a == Type.SCISSORS && b == Type.PAPER) return a;
            if (b == Type.SCISSORS && a == Type.PAPER) return b;
            // paper beats rock
            if (a == Type.PAPER && b == Type.ROCK) return a;
            if (b == Type.PAPER && a == Type.ROCK) return b;
            return null;
        }

        void randomizeVelocity(Entity e) {
            double speed = 0.6 + rand.nextDouble() * 1.6;
            double angle = rand.nextDouble() * Math.PI * 2;
            e.vx = Math.cos(angle) * speed;
            e.vy = Math.sin(angle) * speed;
        }

        void bounceEntities(Entity a, Entity b) {
            // basic swap of velocities component along line connecting centers
            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist == 0) {
                // jitter them apart if overlapping exactly
                a.vx = -a.vx; a.vy = -a.vy;
                b.vx = -b.vx; b.vy = -b.vy;
                return;
            }
            double nx = dx / dist, ny = dy / dist;
            // relative velocity along normal
            double rel = (b.vx - a.vx) * nx + (b.vy - a.vy) * ny;
            if (rel > 0) return; // already separating
            // impulse
            double impulse = -rel * 0.9;
            a.vx -= impulse * nx;
            a.vy -= impulse * ny;
            b.vx += impulse * nx;
            b.vy += impulse * ny;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw entities
            synchronized (entities) {
                for (Entity en : entities) {
                    drawEntity(g, en);
                }
            }

            // draw counters (top-left)
            int rocks = 0, papers = 0, scissors = 0;
            synchronized (entities) {
                for (Entity e : entities) {
                    if (e.type == Type.ROCK) rocks++;
                    else if (e.type == Type.PAPER) papers++;
                    else if (e.type == Type.SCISSORS) scissors++;
                }
            }
            g.setColor(new Color(0,0,0,180));
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String text = String.format("Rock: %d   Paper: %d   Scissors: %d", rocks, papers, scissors);
            g.drawString(text, 8, 22);

            // footer hint
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Click to add one random entity. Use buttons below to control.", 8, height - 8);

            g.dispose();
        }

        void drawEntity(Graphics2D g, Entity e) {
            int cx = (int) Math.round(e.x);
            int cy = (int) Math.round(e.y);
            int r = (int) Math.round(e.radius);

            // background circle (subtle)
            g.setColor(new Color(0,0,0,20));
            g.fillOval(cx - r, cy - r, r*2, r*2);

            // entity rendering by type:
            if (e.type == Type.ROCK) {
                // rock: round grey shape with dark outline
                g.setColor(e.color);
                g.fillOval(cx - r, cy - r, r*2, r*2);
                g.setColor(Color.DARK_GRAY);
                g.setStroke(new BasicStroke(2));
                g.drawOval(cx - r, cy - r, r*2, r*2);
            } else if (e.type == Type.PAPER) {
                // paper: small rectangle with folded corner effect
                int w = (int)(r * 1.6);
                int h = (int)(r * 1.9);
                g.setColor(e.color);
                g.fillRect(cx - w/2, cy - h/2, w, h);
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(cx - w/2, cy - h/2, w, h);
                // folded corner
                g.setColor(new Color(230,230,230));
                Polygon fold = new Polygon();
                fold.addPoint(cx + w/2 - 6, cy - h/2);
                fold.addPoint(cx + w/2, cy - h/2);
                fold.addPoint(cx + w/2, cy - h/2 + 6);
                g.fillPolygon(fold);
                g.setColor(Color.GRAY);
                g.drawPolygon(fold);
            } else {
                // scissors: draw a stylized "X" with handles
                g.setStroke(new BasicStroke(3f));
                // blades (X)
                g.setColor(Color.DARK_GRAY);
                g.drawLine(cx - r, cy - r, cx + r, cy + r);
                g.drawLine(cx + r, cy - r, cx - r, cy + r);
                // handles as circles (red)
                g.setColor(e.color);
                g.fillOval(cx - r - 6, cy - r - 6, 14, 14);
                g.fillOval(cx + r - 8, cy + r - 8, 14, 14);
                g.setColor(Color.BLACK);
                g.drawOval(cx - r - 6, cy - r - 6, 14, 14);
                g.drawOval(cx + r - 8, cy + r - 8, 14, 14);
            }
        }
    }
}
