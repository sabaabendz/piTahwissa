<?php

namespace App\Form;

use App\Entity\Manager;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\NotBlank;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Validator\Constraints\IsTrue;

class ManagerType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $isEdit = $options['is_edit'] ?? false;

        $builder
            ->add('name', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('email', EmailType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('password', PasswordType::class, [
                'mapped' => false,
                'required' => !$isEdit,
                'attr' => [
                    'class' => 'form-control',
                    'autocomplete' => 'new-password',
                ],
                'label_attr' => ['class' => 'form-label'],
                'constraints' => $isEdit ? [] : [
                    new NotBlank([
                        'message' => 'Please enter a password',
                    ]),
                    // Password strength is checked on entity, but here it's unmapped so we keep minimum checks
                    // However, to fully rely on backend validation for mapped fields, we should be careful.
                    // But 'password' field is NOT mapped, so we MUST keep constraints here or handle it manually.
                    // Wait, the User entity has 'password' field. The form maps it as 'false' to handle hashing.
                    // So the Entity constraint on $password property won't trigger automatically if we don't set it.
                    // But we set it in the controller after hashing... this is a common Symfony issue.
                    // If we want "Only PHP Validation", we should map it or validate the raw data.
                    // For now, I will keep standard length constraints on the unmapped field for safety, 
                    // but the REQUEST asked for "Input validation handled ONLY in PHP using Symfony Validator".
                    // The best practice for unmapped password is to use constraints here.
                    new Length([
                        'min' => 8,
                        'minMessage' => 'Your password should be at least {{ limit }} characters',
                        'max' => 4096,
                    ]),
                ],
                'help' => $isEdit ? 'Leave blank to keep current password' : 'Minimum 8 characters',
            ])
            ->add('level', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('department', TextType::class, [
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ]);

        if (!$isEdit) {
            $builder->add('terms', CheckboxType::class, [
                'mapped' => false,
                'constraints' => [
                    new IsTrue([
                        'message' => 'You must agree to the terms of service.',
                    ]),
                ],
                'label' => 'I agree to the terms of service',
                'attr' => ['class' => 'form-check-input'],
                'label_attr' => ['class' => 'form-check-label'],
            ]);
        }
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => Manager::class,
            'is_edit' => false,
        ]);
    }
}
