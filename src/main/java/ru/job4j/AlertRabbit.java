package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 1.1. Job c параметрами [#279185]
 */
public class AlertRabbit {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRabbit.class);

    public static void main(String[] args) {
        AlertRabbit rabbit = new AlertRabbit();
        Properties properties = rabbit.properties();
        try (Connection cn = rabbit.connectBD(properties)) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connect", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(properties.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Подключаемся к базе данных.
     *
     * @param properties свойства.
     * @return свойства.
     * @throws ClassNotFoundException есль нет импорта данного драйвера.
     * @throws SQLException           проблемы с подключение к базе данных.
     */
    private Connection connectBD(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("driver-class-name"));
        return DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password")
        );
    }

    /**
     * Получаем свойста их файла с ресурсами.
     *
     * @return свойства.
     */
    private Properties properties() {
        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return properties;
    }

    /**
     * Действия программы.
     */
    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try (Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connect")) {
                PreparedStatement st = cn.prepareStatement("insert into rabbit (add_date) values (?)");
                st.setDate(1, new Date(System.currentTimeMillis()));
                st.executeUpdate();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
