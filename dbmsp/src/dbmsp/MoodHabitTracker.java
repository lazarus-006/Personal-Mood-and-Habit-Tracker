package dbmsp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MoodHabitTracker {

    static final String DB_URL = "jdbc:mysql://localhost:3306/mood_db?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "dbms";

    JFrame frame;

    public MoodHabitTracker() {
        frame = new JFrame("Personal Mood & Habit Tracker");
        frame.setSize(1000, 600);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Mood Tracker", moodPanel());
        tabs.add("Habit Tracker", habitPanel());
        tabs.add("Daily Log", logPanel());

        frame.add(tabs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // ================= MOOD =================
    JPanel moodPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Date", "Mood", "Notes"}, 0);
        JTable table = new JTable(model);

        JTextField id = new JTextField();
        JTextField date = new JTextField();
        JTextField mood = new JTextField();
        JTextField notes = new JTextField();

        JPanel form = new JPanel(new GridLayout(4,2,10,10));
        form.add(new JLabel("ID")); form.add(id);
        form.add(new JLabel("Date")); form.add(date);
        form.add(new JLabel("Mood")); form.add(mood);
        form.add(new JLabel("Notes")); form.add(notes);

        JPanel btn = new JPanel();
        JButton load = new JButton("Load");
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");

        btn.add(load); btn.add(add); btn.add(update); btn.add(delete);

        Runnable refresh = () -> {
            model.setRowCount(0);
            try (Connection c = getConnection();
                 Statement s = c.createStatement()) {

                ResultSet rs = s.executeQuery("SELECT * FROM mood");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        };

        load.addActionListener(e -> refresh.run());

        add.addActionListener(e -> {
            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement("INSERT INTO mood VALUES (?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.setString(2, date.getText());
                ps.setString(3, mood.getText());
                ps.setString(4, notes.getText());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "✅ Mood Added!");

                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        update.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "⚠ Select a row first!");
                return;
            }

            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE mood SET date=?, mood=?, notes=? WHERE id=?");

                ps.setString(1, date.getText());
                ps.setString(2, mood.getText());
                ps.setString(3, notes.getText());
                ps.setInt(4, Integer.parseInt(id.getText()));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "✏ Mood Updated!");

                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "⚠ Select a row first!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    frame, "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement("DELETE FROM mood WHERE id=?");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "🗑 Mood Deleted!");

                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                id.setText(model.getValueAt(r, 0).toString());
                date.setText(model.getValueAt(r, 1).toString());
                mood.setText(model.getValueAt(r, 2).toString());
                notes.setText(model.getValueAt(r, 3).toString());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }

    // ================= HABIT =================
    JPanel habitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Habit", "Target Days", "Status"}, 0);
        JTable table = new JTable(model);

        JTextField id = new JTextField();
        JTextField habit = new JTextField();
        JTextField days = new JTextField();
        JTextField status = new JTextField();

        JPanel form = new JPanel(new GridLayout(4,2,10,10));
        form.add(new JLabel("ID")); form.add(id);
        form.add(new JLabel("Habit")); form.add(habit);
        form.add(new JLabel("Target Days")); form.add(days);
        form.add(new JLabel("Status")); form.add(status);

        JPanel btn = new JPanel();
        JButton load = new JButton("Load");
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");

        btn.add(load); btn.add(add); btn.add(update); btn.add(delete);

        Runnable refresh = () -> {
            model.setRowCount(0);
            try (Connection c = getConnection();
                 Statement s = c.createStatement()) {

                ResultSet rs = s.executeQuery("SELECT * FROM habit");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getString(4)
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        };

        load.addActionListener(e -> refresh.run());

        add.addActionListener(e -> {
            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement("INSERT INTO habit VALUES (?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.setString(2, habit.getText());
                ps.setInt(3, Integer.parseInt(days.getText()));
                ps.setString(4, status.getText());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "✅ Habit Added!");

                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        update.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "⚠ Select a row first!");
                return;
            }

            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement(
                        "UPDATE habit SET habit_name=?, target_days=?, status=? WHERE id=?");

                ps.setString(1, habit.getText());
                ps.setInt(2, Integer.parseInt(days.getText()));
                ps.setString(3, status.getText());
                ps.setInt(4, Integer.parseInt(id.getText()));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "✏ Habit Updated!");

                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        delete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(frame, "⚠ Select a row first!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    frame, "Delete this record?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            try (Connection c = getConnection()) {
                PreparedStatement ps = c.prepareStatement("DELETE FROM habit WHERE id=?");
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame, "🗑 Habit Deleted!");

                refresh.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                id.setText(model.getValueAt(r, 0).toString());
                habit.setText(model.getValueAt(r, 1).toString());
                days.setText(model.getValueAt(r, 2).toString());
                status.setText(model.getValueAt(r, 3).toString());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }

    // ================= DAILY LOG =================
    JPanel logPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "Mood", "Habit", "Status"}, 0);
        JTable table = new JTable(model);

        JButton load = new JButton("Load Combined Data");

        load.addActionListener(e -> {
            model.setRowCount(0);
            try (Connection c = getConnection();
                 Statement s = c.createStatement()) {

                ResultSet rs = s.executeQuery(
                        "SELECT m.date, m.mood, h.habit_name, h.status FROM mood m, habit h");

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)
                    });
                }

                JOptionPane.showMessageDialog(frame, "📊 Data Loaded!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(load, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoodHabitTracker::new);
    }
}                                           