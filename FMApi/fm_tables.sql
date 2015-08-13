SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

-- --------------------------------------------------------

--
-- Table structure for table `fm_chats`
--

CREATE TABLE IF NOT EXISTS `fm_chats` (
  `id` int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `admin_id` int(10) NOT NULL,
  `theme` varchar(200) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `fm_msgs`
--

CREATE TABLE IF NOT EXISTS `fm_msgs` (
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `chat_id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `text` varchar(3000) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `fm_refs`
--

CREATE TABLE IF NOT EXISTS `fm_refs` (
  `user_id` int(10) NOT NULL,
  `chat_id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `fm_users`
--

CREATE TABLE IF NOT EXISTS `fm_users` (
  `id` int(10) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `socnet` int(1) NOT NULL,
  `socuserid` bigint(20) NOT NULL,
  `name` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 
  
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
