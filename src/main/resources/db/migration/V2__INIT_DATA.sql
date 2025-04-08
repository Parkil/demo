INSERT INTO company (credits, name, connected_service_pricing_id) VALUES (5000, 'A사', null);
INSERT INTO company (credits, name, connected_service_pricing_id) VALUES (10000, 'B사', null);
INSERT INTO company (credits, name, connected_service_pricing_id) VALUES (10000, 'C사', null);

INSERT INTO feature_info (code, deduction_credits, deduction_criteria, limited_amount, restriction_criteria, usage_criteria, name) VALUES ('F_01', 10, 0, 2000, 0, 0, 'AI 번역');
INSERT INTO feature_info (code, deduction_credits, deduction_criteria, limited_amount, restriction_criteria, usage_criteria, name) VALUES ('F_02', 10, 0, 1000, 0, 0, 'AI 교정');
INSERT INTO feature_info (code, deduction_credits, deduction_criteria, limited_amount, restriction_criteria, usage_criteria, name) VALUES ('F_03', 20, 0, 1500, 0, 0, 'AI 뉘앙스 조절');
INSERT INTO feature_info (code, deduction_credits, deduction_criteria, limited_amount, restriction_criteria, usage_criteria, name) VALUES ('F_04', 50, 0, 200, 1, 1, 'AI 초안 작성');

