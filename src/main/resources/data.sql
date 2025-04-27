-- 初始化幣別表數據
MERGE INTO CURRENCY KEY(CODE) VALUES
('USD', '美元', 'United States Dollar'),
('GBP', '英鎊', 'British Pound Sterling'),
('EUR', '歐元', 'Euro'),
('JPY', '日圓', 'Japanese Yen'); 